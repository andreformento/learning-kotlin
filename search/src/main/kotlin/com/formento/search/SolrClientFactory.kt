package com.formento.search

import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.impl.CloudSolrClient
import org.apache.solr.client.solrj.impl.Http2SolrClient
import org.apache.solr.client.solrj.request.QueryRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration
import java.util.*


data class SolrClusterProperties(val zkHosts: List<String>, val zkChroot: String?)
data class SolrWarmupAttemptProperties(val max: Int, val sleep: Duration)
data class SolrStandaloneProperties(val host: String, val port: Int)

@ConstructorBinding
@ConfigurationProperties(prefix = "solr")
data class SolrProperties(
    val warmupAttempt: SolrWarmupAttemptProperties,
    val cluster: SolrClusterProperties?,
    val standalone: SolrStandaloneProperties?,
)

private data class CreatedSolrClient(val solrClient: SolrClient, val connectFunction: () -> Boolean)

private class SolrHealthCheck(
    private val warmupAttempt: SolrWarmupAttemptProperties,
    private val createdSolrClient: CreatedSolrClient,
) {
    private fun isAlive(
        attemptCount: Int,
        isHealth: () -> Boolean
    ): Boolean =
        if (attemptCount <= 0) {
            false
        } else {
            try {
                if (isHealth()) {
                    true
                } else {
                    isAlive(attemptCount - 1, isHealth)
                }
            } catch (e: Exception) {
                if (attemptCount <= 0) {
                    SolrClientFactory.LOGGER.error("Cannot run Solr health check after ${warmupAttempt.max} retries", e)
                    false
                } else {
                    SolrClientFactory.LOGGER.error("Retry $attemptCount to run Solr health check", e)
                    Thread.sleep(warmupAttempt.sleep.toMillis())
                    isAlive(attemptCount - 1, isHealth)
                }
            }
        }


    fun isAlive(): Boolean =
        if (isAlive(warmupAttempt.max, createdSolrClient.connectFunction)) {
            isAlive(warmupAttempt.max) {
                SolrQuery()
                    .apply { setParam("q", "*:*") }
                    .let { QueryRequest(it) }
                    .process(createdSolrClient.solrClient, "products")
                    .results
                    .numFound > 0
            }
        } else {
            false
        }
}

@Configuration
class SolrClientFactory {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(SolrClientFactory::class.java)
    }

    @Bean
    fun createSolrClient(solrProperties: SolrProperties): SolrClient =
        if (solrProperties.standalone == null) {
            CloudSolrClient
                .Builder(solrProperties.cluster!!.zkHosts, Optional.ofNullable(solrProperties.cluster!!.zkChroot))
                .build()
                .let { solrClient ->
                    CreatedSolrClient(solrClient) {
                        solrClient.connect()
                        true
                    }
                }

        } else {
            Http2SolrClient
                .Builder("http://${solrProperties.standalone.host}:${solrProperties.standalone.port}/solr")
                .build()
                .let { solrClient ->
                    CreatedSolrClient(solrClient) {
                        solrClient.ping()
                        true
                    }
                }
        }
            .also {
                if (!SolrHealthCheck(warmupAttempt = solrProperties.warmupAttempt, createdSolrClient = it).isAlive()) {
                    throw RuntimeException("Solr is not alive")
                }
            }
            .solrClient

}

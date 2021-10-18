package com.formento.search

import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.impl.CloudSolrClient
import org.apache.solr.client.solrj.impl.Http2SolrClient
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

@Configuration
class SolrClientFactory {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(SolrClientFactory::class.java)
    }

    private fun solrHealthCheck(
        warmupAttempt: SolrWarmupAttemptProperties,
        attemptCount: Int,
        checker: () -> Unit
    ): Unit =
        try {
            checker()
        } catch (e: Exception) {
            if (attemptCount <= 0) {
                LOGGER.error("Cannot connect on Solr after ${warmupAttempt.max} retries", e)
                throw e
            } else {
                LOGGER.error("Retry $attemptCount to connect on Solr", e)
                Thread.sleep(warmupAttempt.sleep.toMillis())
                solrHealthCheck(warmupAttempt, attemptCount - 1, checker)
            }
        }


    private fun solrHealthCheck(warmupAttempt: SolrWarmupAttemptProperties, checker: () -> Unit) =
        solrHealthCheck(warmupAttempt, warmupAttempt.max, checker)

    @Bean
    fun createSolrClient(solrProperties: SolrProperties): SolrClient =
        if (solrProperties.standalone == null) {
            CloudSolrClient
                .Builder(solrProperties.cluster!!.zkHosts, Optional.ofNullable(solrProperties.cluster!!.zkChroot))
                .build()
                .also { solrHealthCheck(solrProperties.warmupAttempt) { it.connect() } }
        } else {
            Http2SolrClient
                .Builder("http://${solrProperties.standalone.host}:${solrProperties.standalone.port}/solr")
                .build()
                .also { solrHealthCheck(solrProperties.warmupAttempt) { it.ping("products") } }
        }

}

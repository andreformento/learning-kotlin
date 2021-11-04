package com.formento.search.repository

import com.formento.search.util.Retry
import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.impl.CloudSolrClient
import org.apache.solr.client.solrj.impl.Http2SolrClient
import org.apache.solr.client.solrj.request.QueryRequest
import org.apache.solr.client.solrj.response.SolrPingResponse
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration
import java.util.*

data class SolrCollectionProperties(val name: String)
data class SolrClusterProperties(val zkHosts: List<String>, val zkChroot: String?)
data class SolrWarmupAttemptProperties(val tryAtLeast: Int, val sleep: Duration)
data class SolrStandaloneProperties(val host: String, val port: Int)

@ConstructorBinding
@ConfigurationProperties(prefix = "solr")
data class SolrProperties(
    val collection: SolrCollectionProperties,
    val warmupAttempt: SolrWarmupAttemptProperties,
    val cluster: SolrClusterProperties?,
    val standalone: SolrStandaloneProperties?,
)

private data class CreatedSolrClient(val solrClient: SolrClient, val pingFunction: () -> SolrPingResponse)

private class SolrHealthCheck(
    private val warmupAttempt: SolrWarmupAttemptProperties,
    private val createdSolrClient: CreatedSolrClient,
) {

    private fun ping() =
        createdSolrClient
            .pingFunction()
            .response
            .get("status")
            .equals("OK")

    private fun check() =
        SolrQuery()
            .apply { setParam("q", "*:*") }
            .let { QueryRequest(it) }
            .process(createdSolrClient.solrClient)
            .results
            .numFound > 0

    fun isHealth(): Boolean =
        Retry(sleepTime = warmupAttempt.sleep, tryAtLeast = warmupAttempt.tryAtLeast).let {
            if (it.tryToRun(::ping)) {
                it.tryToRun(::check)
            } else {
                false
            }
        }

}

@Configuration
class SolrClientFactory(private val solrProperties: SolrProperties) {
    @Bean
    fun createSolrClient(): SolrClient =
        if (solrProperties.standalone == null) {
            CloudSolrClient
                .Builder(solrProperties.cluster!!.zkHosts, Optional.ofNullable(solrProperties.cluster!!.zkChroot))
                .build()
                .let { solrClient ->
                    solrClient.defaultCollection = solrProperties.collection.name

                    CreatedSolrClient(solrClient) {
                        solrClient.connect()
                        solrClient.ping()
                    }
                }

        } else {
            Http2SolrClient
                .Builder("http://${solrProperties.standalone.host}:${solrProperties.standalone.port}/solr/${solrProperties.collection.name}")
                .build()
                .let { solrClient -> CreatedSolrClient(solrClient) { solrClient.ping() } }
        }
            .also {
                if (!SolrHealthCheck(warmupAttempt = solrProperties.warmupAttempt, createdSolrClient = it).isHealth()) {
                    throw RuntimeException("Solr is not alive")
                }
            }
            .solrClient

}

package com.formento.search

import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.impl.CloudSolrClient
import org.apache.solr.client.solrj.impl.Http2SolrClient
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*


data class SolrClusterProperties(val zkHosts: List<String>, val zkChroot: String?)
data class SolrStandaloneProperties(val host: String, val port: Int)

@ConstructorBinding
@ConfigurationProperties(prefix = "solr")
data class SolrProperties(
    val cluster: SolrClusterProperties?,
    val standalone: SolrStandaloneProperties?,
)

@Configuration
class SolrClientFactory {

    @Bean
    fun createSolrClient(solrProperties: SolrProperties): SolrClient =
        if (solrProperties.standalone == null) {
            CloudSolrClient
                .Builder(solrProperties.cluster!!.zkHosts, Optional.ofNullable(solrProperties.cluster!!.zkChroot))
                .build()
                .also { it.connect() }
        } else {
            Http2SolrClient
                .Builder("http://${solrProperties.standalone.host}:${solrProperties.standalone.port}/solr")
                .build()
        }

}

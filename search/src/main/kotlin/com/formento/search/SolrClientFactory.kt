package com.formento.search

import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.impl.Http2SolrClient
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "solr")
@ConstructorBinding
data class SolrProperties(val host: String, val port: Int)

@Configuration
class SolrClientFactory {

    @Bean
    fun createSolrClient(solrProperties: SolrProperties): SolrClient =
        Http2SolrClient.Builder("http://${solrProperties.host}:${solrProperties.port}/solr").build()

}

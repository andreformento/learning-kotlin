package com.formento.search

import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.impl.Http2SolrClient
import org.apache.solr.client.solrj.response.SolrPingResponse
import org.junit.ClassRule
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.SolrContainer
import org.testcontainers.utility.DockerImageName

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@DirtiesContext
abstract class IntegrationTest {

    companion object {

        @ClassRule
        val solrContainer = SolrContainer(DockerImageName.parse("solr:8.10.0"))

        @DynamicPropertySource
        @JvmStatic
        fun setProperties(registry: DynamicPropertyRegistry) {
            registry.add("solr.url") { "http://${solrContainer.containerIpAddress}:${solrContainer.solrPort}/solr" }
        }

        @BeforeAll
        @JvmStatic
        internal fun beforeAll() {
            solrContainer.start()
            val client: SolrClient = Http2SolrClient.Builder(
                "http://${solrContainer.containerIpAddress}:${solrContainer.solrPort}/solr"
            ).build()

            val response: SolrPingResponse = client.ping("dummy")
            println(response.elapsedTime)
        }

        @AfterAll
        @JvmStatic
        internal fun afterAll() {
            solrContainer.close()
        }
    }

    @Bean
    fun mockSolrClient(): SolrClient =
        Http2SolrClient.Builder("http://${solrContainer.containerIpAddress}:${solrContainer.solrPort}/solr").build()

    @AfterEach
    fun cleanUp() {
    }

}

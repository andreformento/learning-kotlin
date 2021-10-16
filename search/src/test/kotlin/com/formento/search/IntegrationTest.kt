package com.formento.search

import org.junit.ClassRule
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
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
        }

        @AfterAll
        @JvmStatic
        internal fun afterAll() {
            solrContainer.close()
        }
    }

    @BeforeEach
    fun ping() {

    }

    @AfterEach
    fun cleanUp() {
    }

}

package com.formento.search

import org.assertj.core.api.Assertions.assertThat
import org.junit.ClassRule
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.SolrContainer
import org.testcontainers.utility.DockerImageName
import java.io.File


@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@DirtiesContext
class SearchRepositoryTest {

    companion object {

        @ClassRule
        val solrContainer = SolrContainer(DockerImageName.parse("solr:8.10.0"))

//        @ClassRule
//        var environment: DockerComposeContainer<KDockerComposeContainer> =
//            DockerComposeContainer(File("docker-compose.yml"))
//                .withExposedService("redis_1", REDIS_PORT)
//                .withExposedService("elasticsearch_1", ELASTICSEARCH_PORT)

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


    @Test
    fun `should search`() {
        assertThat("a").isEqualTo("a")
    }

}

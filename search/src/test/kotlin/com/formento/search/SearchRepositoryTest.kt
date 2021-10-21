package com.formento.search

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.ClassRule
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
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
        private val instance: KDockerComposeContainer by lazy { defineDockerCompose()}

        class KDockerComposeContainer(file: File) : DockerComposeContainer<KDockerComposeContainer>(file)

        private fun defineDockerCompose() = KDockerComposeContainer(File("docker-compose.yml"))
//            .withExposedService("solr1_1",8983)

        @BeforeAll
        @JvmStatic
        internal fun beforeAll() {
            instance.start()
//            println(instance.getServicePort("solr1_1",8981))
        }

        @AfterAll
        @JvmStatic
        internal fun afterAll() {
            instance.stop()
        }
    }


//    companion object {
//
//        @ClassRule
//        val solrContainer = SolrContainer(DockerImageName.parse("solr:8.10.0"))
//
////        @ClassRule
////        var environment: DockerComposeContainer<KDockerComposeContainer> =
////            DockerComposeContainer(File("docker-compose.yml"))
////                .withExposedService("redis_1", REDIS_PORT)
////                .withExposedService("elasticsearch_1", ELASTICSEARCH_PORT)
//
//        @DynamicPropertySource
//        @JvmStatic
//        fun setProperties(registry: DynamicPropertyRegistry) {
//            registry.add("solr.url") { "http://${solrContainer.containerIpAddress}:${solrContainer.solrPort}/solr" }
//        }
//
//        @BeforeAll
//        @JvmStatic
//        internal fun beforeAll() {
//            solrContainer.start()
//        }
//
//        @AfterAll
//        @JvmStatic
//        internal fun afterAll() {
//            solrContainer.close()
//        }
//    }


    @Autowired
    lateinit var repo: Products

    @Test
    fun `should search`(): Unit = runBlocking {
        val searchResult = repo.search(SearchParams(query = "First"))

        assertThat(searchResult.hits).isGreaterThan(0)
    }

}

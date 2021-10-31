package com.formento.search.repository

import com.formento.search.api.ProductSearchParams
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File

@SpringBootTest
@Testcontainers
class ProductsTest {

    companion object {
        class KDockerComposeContainer(files: List<File>) : DockerComposeContainer<KDockerComposeContainer>(files)

        private fun defineDockerCompose() = KDockerComposeContainer(
            listOf(File("docker-compose.yml"), File("docker-compose.test.yml"))
        )

        private val instance: KDockerComposeContainer by lazy { defineDockerCompose() }

        @DynamicPropertySource
        @JvmStatic
        fun setProperties(registry: DynamicPropertyRegistry) {
            val solrContainer = instance.getContainerByServiceName("solr_1").orElse(null)

            registry.add("solr.standalone.host") { solrContainer.containerIpAddress }
            registry.add("solr.standalone.port") { solrContainer.boundPortNumbers.first() }
        }

        @BeforeAll
        @JvmStatic
        internal fun beforeAll() {
            instance.start()
        }

        @AfterAll
        @JvmStatic
        internal fun afterAll() {
            instance.stop()
        }
    }

    @Autowired
    lateinit var repo: Products

    @Test
    fun `should search`(): Unit = runBlocking {
        val searchResult = repo.search(ProductSearchParams(query = "First"))

        assertThat(searchResult.hits).isGreaterThan(0)
    }

}

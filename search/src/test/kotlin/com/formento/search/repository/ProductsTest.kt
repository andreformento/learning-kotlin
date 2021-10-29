package com.formento.search.repository

import com.formento.search.api.ProductSearchParams
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
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
import java.io.File

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@DirtiesContext
class ProductsTest {

    companion object {
        private val instance: KDockerComposeContainer by lazy { defineDockerCompose() }

        class KDockerComposeContainer(files: List<File>) : DockerComposeContainer<KDockerComposeContainer>(files)

        private fun defineDockerCompose() = KDockerComposeContainer(
            listOf(File("docker-compose.yml"), File("docker-compose.test.yml"))
        )

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

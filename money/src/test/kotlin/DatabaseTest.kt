package com.andreformento.money

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.r2dbc.core.DatabaseClient
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
class DatabaseTest {

    @Autowired
    lateinit var databaseClient: DatabaseClient

    @Test
    fun `when database is connected then it should be Postgres version 13`() {
        val actualDatabaseVersion = databaseClient
            .sql("SELECT version()")
            .fetch()
            .one()
            .map { it.values.first() }
            .block()
            .toString()
        assertThat(actualDatabaseVersion).contains("PostgreSQL 13")
    }

    @Test
    fun `should have tables schema configured`() {
        val tableCount =
            databaseClient
                .sql("select count(*) from information_schema.tables where table_schema = 'public'")
                .fetch()
                .one()
                .map { it.values.first() }
                .block()
                .toString()
                .toInt()
        assertThat(tableCount).isGreaterThan(0)
    }

}

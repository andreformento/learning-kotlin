package com.andreformento.myapp

import kotlinx.coroutines.flow.Flow
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

@SpringBootApplication
class MyappApplication

fun main(args: Array<String>) {
	runApplication<MyappApplication>(*args)
}

data class User(val id: String, val firstname: String)

interface CoroutineRepository : CoroutineCrudRepository<User, String> {

	suspend fun findOne(id: String): User

	fun findByFirstname(firstname: String): Flow<User>

	suspend fun findAllByFirstname(id: String): List<User>
}

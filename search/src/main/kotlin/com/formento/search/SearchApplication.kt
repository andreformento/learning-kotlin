package com.formento.search

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class SearchApplication

fun main(args: Array<String>) {
	runApplication<SearchApplication>(*args)
}

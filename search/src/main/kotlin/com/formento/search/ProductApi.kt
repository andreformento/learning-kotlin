package com.formento.search

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/products", produces = ["application/json"])
class ProductApi(private val products: Products) {

    @GetMapping
    suspend fun search(@RequestParam("q") query: String?): ProductSearchResult =
        products.search(SearchParams(query = query))

}

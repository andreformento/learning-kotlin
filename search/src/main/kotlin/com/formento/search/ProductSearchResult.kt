package com.formento.search

data class ProductDocument(val id: String, val title: String)
data class ProductSearchResult(val hits: Long, val products: List<ProductDocument>)

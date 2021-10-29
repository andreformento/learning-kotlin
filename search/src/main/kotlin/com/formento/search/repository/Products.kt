package com.formento.search.repository

import com.formento.search.api.ProductDocument
import com.formento.search.api.ProductSearchParams
import com.formento.search.api.ProductSearchResult
import io.micrometer.core.instrument.MeterRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.SolrRequest
import org.apache.solr.client.solrj.request.QueryRequest
import org.apache.solr.client.solrj.response.QueryResponse
import org.springframework.stereotype.Component

fun QueryResponse.toModel() = ProductSearchResult(
    hits = results.numFound,
    products = results.map {
        ProductDocument(
            id = it["id"]!!.toString(),
            title = it["title_s"]!!.toString(),
        )
    },
)

@Component
class Products(private val solrClient: SolrClient, private val meterRegistry: MeterRegistry) {

    suspend fun search(productSearchParams: ProductSearchParams): ProductSearchResult {
        val solrQuery = SolrQuery().apply {
            if (productSearchParams.query == null || productSearchParams.query.isEmpty()) {
                setParam("q.alt", "*:*")
            } else {
                query = "title:${productSearchParams.query}"
            }
            setParam("q.op", "OR")
        }

        val req: SolrRequest<QueryResponse> = QueryRequest(solrQuery)

        val queryResponse = withContext(Dispatchers.IO) {
            meterRegistry.timer("product-search").recordCallable {
                req.process(solrClient)
            }
        }

        return queryResponse!!
            .toModel()
            .also {
                if (it.hits == 0L) {
                    meterRegistry.counter("product-zero-result-page-search").increment()
                }
            }
    }

}

package services.apis

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import model.SearchResult
import model.SearchResultCollection

abstract class AbstractSearchApi(private val hostUrl: String) : SearchApi {
    private val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    override fun getTopResults(query: String): List<SearchResult> {
        val results = runBlocking {
            client.get<SearchResultCollection> {
                url(hostUrl)
                parameter("query", query)
            }
        }
        return results.list
    }
}


package services

import com.maximeroussy.invitrode.WordGenerator
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.delay
import model.SearchResult
import model.SearchResultCollection
import model.SearchSource
import org.slf4j.LoggerFactory
import kotlin.random.Random
import kotlin.random.nextUInt


class StubSearchEngineService(private val source: SearchSource, port: Int) {
    private val logger = LoggerFactory.getLogger(source.name)
    private val server = embeddedServer(Netty, port) {
        install(ContentNegotiation) {
            json()
        }
        routing {
            get("/search") {
                val query = call.request.queryParameters["query"] ?: ""
                val delay = Random.nextLong(1, 5000)
                logger.debug("Delay: $delay ms")
                delay(delay)
                call.respond(SearchResultCollection(generateResults(query)))
            }
        }
    }

    private fun generateResults(query: String): List<SearchResult> {
        val size = Random.nextInt(5, 10)
        val results = generateSequence {
            SearchResult(
                source = source,
                link = generateRandomLink(query),
                views = Random.nextUInt().toLong()
            )
        }.take(size).toList()
        return results
    }

    private fun generateRandomLink(query: String): String {
        val size = Random.nextInt(3, 10)
        val generator = WordGenerator()
        val words = generateSequence { generator.newWord(Random.nextInt(3, 10)) }
            .take(size)
            .plus(query)
        return words.joinToString(" ")
    }

    fun start() {
        server.start()
    }
}
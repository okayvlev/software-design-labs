import configs.SearchServiceConfig
import configs.ServersConfig
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.pipeline.*
import model.SearchResult
import model.SearchResultCollection
import model.SearchSource
import org.junit.Assert
import services.SearchService


internal val defaultServersConfig = ServersConfig(
    "http://localhost:8080/google",
    "http://localhost:8080/yandex",
    "http://localhost:8080/bing"
)

internal fun initServer(port: Int, init: Routing.() -> Unit): NettyApplicationEngine {
    return embeddedServer(Netty, port) {
        install(ContentNegotiation) {
            json()
        }
        routing {
            init()
        }
    }
}

internal fun withServer(init: Routing.() -> Unit, block: () -> Unit) {
    val mockServer = initServer(8080) {
        init()
    }
    try {
        mockServer.start()
        block()
    } finally {
        mockServer.stop(0, 0)
    }
}

internal val defaultRoutingConfig: Routing.() -> Unit = {
    get("/google") {
        withQuery { query ->
            call.respond(getDefaultResponse(SearchSource.Google, query))
        }
    }
    get("/yandex") {
        withQuery { query ->
            call.respond(getDefaultResponse(SearchSource.Yandex, query))
        }
    }
    get("/bing") {
        withQuery { query ->
            call.respond(getDefaultResponse(SearchSource.Bing, query))
        }
    }
}

internal suspend fun RoutingContext.withQuery(block: suspend RoutingContext.(query: String) -> Unit) {
    val query = call.request.queryParameters["query"] ?: ""
    block(query)
}

internal fun getDefaultResponse(source: SearchSource, query: String) = SearchResultCollection(
    (1..5).map { index ->
        SearchResult(source, query + source.name + index, index.toLong())
    }
)

internal fun getDefaultService(): SearchService {
    return SearchService(
        SearchServiceConfig(1000, 2000),
        defaultServersConfig
    )
}


internal fun assertEqualResults(expected: List<SearchResult>, actual: List<SearchResult>) {
    Assert.assertEquals(expected.sortedBy { it.link }, actual.sortedBy { it.link })
}

internal fun getFullExpectedResponse(query: String) = getDefaultResponse(SearchSource.Google, query).list +
        getDefaultResponse(SearchSource.Yandex, query).list +
        getDefaultResponse(SearchSource.Bing, query).list



typealias RoutingContext = PipelineContext<Unit, ApplicationCall>
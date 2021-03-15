import configs.SearchServiceConfig
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.delay
import model.SearchSource.*
import org.junit.Test
import services.SearchService


class TimeoutTests {

    @Test
    fun `one service timed out`() = withServer({
        get("/google") {
            withQuery { query ->
                call.respond(getDefaultResponse(Google, query))
            }
        }
        get("/yandex") {
            withQuery { query ->
                call.respond(getDefaultResponse(Yandex, query))
            }
        }
        get("/bing") {
            withQuery { query ->
                delay(2000)
                call.respond(getDefaultResponse(Bing, query))
            }
        }
    }) {
        val query = "query"
        val actual = getDefaultService().search(query)
        val expected = getDefaultResponse(Google, query).list +
                getDefaultResponse(Yandex, query).list

        assertEqualResults(expected, actual)
    }

    @Test
    fun `all services timed out`() = withServer({
        get("/google") {
            withQuery { query ->
                delay(2000)
                call.respond(getDefaultResponse(Google, query))
            }
        }
        get("/yandex") {
            withQuery { query ->
                delay(2000)
                call.respond(getDefaultResponse(Yandex, query))
            }
        }
        get("/bing") {
            withQuery { query ->
                delay(2000)
                call.respond(getDefaultResponse(Bing, query))
            }
        }
    }) {
        val query = "query"
        val actual = getDefaultService().search(query)

        assertEqualResults(emptyList(), actual)
    }

    @Test
    fun `sequential receive timeout success`() = withServer({
        get("/google") {
            withQuery { query ->
                delay(1000)
                call.respond(getDefaultResponse(Google, query))
            }
        }
        get("/yandex") {
            withQuery { query ->
                delay(2000)
                call.respond(getDefaultResponse(Yandex, query))
            }
        }
        get("/bing") {
            withQuery { query ->
                delay(3000)
                call.respond(getDefaultResponse(Bing, query))
            }
        }
    }) {
        val query = "query"
        val service = SearchService(
            SearchServiceConfig(2000, 4000),
            defaultServersConfig
        )
        val actual = service.search(query)
        val expected = getFullExpectedResponse(query)

        assertEqualResults(expected, actual)
    }

    @Test
    fun `sequential receive timeout fail`() = withServer({
        get("/google") {
            withQuery { query ->
                delay(500)
                call.respond(getDefaultResponse(Google, query))
            }
        }
        get("/yandex") {
            withQuery { query ->
                delay(1000)
                call.respond(getDefaultResponse(Yandex, query))
            }
        }
        get("/bing") {
            withQuery { query ->
                delay(3000)
                call.respond(getDefaultResponse(Bing, query))
            }
        }
    }) {
        val query = "query"
        val service = SearchService(
            SearchServiceConfig(1000, 3500),
            defaultServersConfig
        )
        val actual = service.search(query)
        val expected = getDefaultResponse(Google, query).list +
                getDefaultResponse(Yandex, query).list

        assertEqualResults(expected, actual)
    }
}


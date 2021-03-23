package services.exchange

import io.ktor.application.*
import io.ktor.client.features.json.serializer.*
import io.ktor.content.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import services.IssueRequest
import services.ExchangeTradeRequest


class StockExchangeServer {
    private val server = embeddedServer(Netty, 8080) {
        install(ContentNegotiation) {
            json()
        }
        routing {
            post("/issue") {
                val form = call.receive<IssueRequest>()
                runService {
                    service.issueStock(form.name, form.price, form.amount)
                }
            }
            get("/info") {
                val name = call.request.queryParameters["stockName"]
                if (name == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                runCatching {
                    service.getStockInfo(name)
                }.onFailure {
                    call.respond(HttpStatusCode.BadRequest, it.message.toString())
                }.onSuccess { info ->
                    call.respond(ByteArrayContent(
                        Json.encodeToString(info).encodeToByteArray(),
                        ContentType.Application.Json,
                        HttpStatusCode.OK
                    ))
                }
            }
            post("/buy") {
                val form = call.receive<ExchangeTradeRequest>()
                runService {
                    service.buyShares(form.stockName, form.amount)
                }
            }
            post("/sell") {
                val form = call.receive<ExchangeTradeRequest>()
                runService {
                    service.sellShares(form.stockName, form.amount)
                }
            }
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.runService(block: () -> Unit) {
        runCatching(block)
            .onFailure { call.respond(HttpStatusCode.BadRequest, it.message.toString()) }
            .onSuccess { call.respond(HttpStatusCode.OK) }
    }

    private val service = StockExchangeService()

    fun start() {
        server.start()
    }
}
package services.ui

import config.AccountServiceConfig
import io.ktor.application.*
import io.ktor.content.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import services.AccountTradeRequest
import services.InvestRequest
import services.RegisterRequest


class AccountServer(config: AccountServiceConfig) {
    private val server = embeddedServer(Netty, config.port) {
        install(ContentNegotiation) {
            json()
        }
        routing {
            post("/register") {
                val form = call.receive<RegisterRequest>()
                service.addAccount(form.username, form.money ?: 0.0)
                call.respond(HttpStatusCode.OK)
            }
            post("/invest") {
                val form = call.receive<InvestRequest>()
                service.addMoney(form.username, form.money)
                call.respond(HttpStatusCode.OK)
            }
            get("/getShares") {
                val name = call.request.queryParameters["username"]
                if (name == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val info = service.getShares(name)
                call.respond(jsonContent(Json.encodeToString(info)))
            }
            get("/getTotal") {
                val name = call.request.queryParameters["username"]
                if (name == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val info = service.getTotal(name)
                call.respond(jsonContent(Json.encodeToString(info)))
            }
            post("/buy") {
                val form = call.receive<AccountTradeRequest>()
                service.buyShares(form.username, form.stockName, form.amount)
                call.respond(HttpStatusCode.OK)
            }
            post("/sell") {
                val form = call.receive<AccountTradeRequest>()
                service.sellShares(form.username, form.stockName, form.amount)
                call.respond(HttpStatusCode.OK)
            }
        }
    }

    private fun jsonContent(str: String): ByteArrayContent {
        return ByteArrayContent(
            str.encodeToByteArray(),
            ContentType.Application.Json,
            HttpStatusCode.OK
        )
    }

    private val service = AccountService(config)

    fun start() {
        server.start()
    }
}
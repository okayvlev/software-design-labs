package services.ui

import config.AccountServiceConfig

fun main() {
    val config = AccountServiceConfig("http://localhost:8080", 80)
    val server = AccountServer(config)
    server.start()
}
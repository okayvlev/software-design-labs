package services

import configs.ServerConfig
import repository.StoreRepository


import io.reactivex.netty.protocol.http.server.HttpServer
import model.Currency
import model.ItemRequest
import model.Price
import model.UserRequest
import rx.Observable


class StoreService(serverConfig: ServerConfig, private val repository: StoreRepository) {
    private val server = HttpServer.newServer(serverConfig.port)


    private fun dispatch(path: String, params: QueryParameters): Observable<String> {
        return when (path) {
            "/registerUser" -> RegisterUserHandler.handle(params, repository)
            "/getUser" -> GetUserHandler.handle(params, repository)
            "/addItem" -> AddItemHandler.handle(params, repository)
            "/getItems" -> GetItemsHandler.handle(params, repository)
            else -> return Observable.just("Endpoint not found")
        }
    }

    fun start() {
        server.start { request, response ->
            val path = request.decodedPath
            val res = dispatch(path, request.queryParameters)
            response.writeString(res)
        }.awaitShutdown()
    }
}

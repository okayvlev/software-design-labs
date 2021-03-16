package services

import model.Currency
import model.ItemRequest
import model.Price
import model.UserRequest
import repository.StoreRepository
import rx.Observable


typealias QueryParameters = Map<String, List<String>>

interface QueryHandler {

    fun Map<String, List<String>>.getParam(name: String): String? = get(name)?.firstOrNull()

    fun extractPrices(params: QueryParameters): List<Price> {
        val lowerCaseParams = params.mapKeys { (k, _) -> k.toLowerCase() }
        return Currency.values().map { ccy ->
                lowerCaseParams[ccy.name.toLowerCase()]?.firstOrNull()?.toDouble() to ccy
        }
            .filter { it.first != null }
            .map { (value, ccy) -> Price(value!!, ccy) }
    }

    fun handle(params: QueryParameters, repository: StoreRepository): Observable<String>
}

/**
 * Registers a new user and their preferred currency
 */
object RegisterUserHandler : QueryHandler {

    override fun handle(params: QueryParameters, repository: StoreRepository): Observable<String> {
        val user = UserRequest(
            params.getParam("name")
                ?: return Observable.just("name is null"),
            params.getParam("currency")?.let { ccy ->
                Currency.values().firstOrNull { it.name.equals(ccy, true) }
            } ?: return Observable.just("currency is null")
        )

        val res = repository.registerUser(user)
        return res.map { it.toString() }
    }
}

/**
 * Returns user object by name
 */
object GetUserHandler : QueryHandler {
    override fun handle(params: QueryParameters, repository: StoreRepository): Observable<String> {
        val userName = params.getParam("userName")
            ?: return Observable.just("userName is null")

        val res = repository.getUserByName(userName)
        return res.map { it.toString() }
    }
}

/**
 * Adds an item with prices in all specified currencies
 */
object AddItemHandler : QueryHandler {
    override fun handle(params: QueryParameters, repository: StoreRepository): Observable<String> {
        val item = ItemRequest(
            params.getParam("title")
                ?: return Observable.just("title is null"),
            extractPrices(params)
        )

        val res = repository.addItem(item)
        return res.map { it.toString() }
    }
}

/**
 * Returns all items with prices (or nulls) in corresponding to specified user currency
 */
object GetItemsHandler : QueryHandler {
    override fun handle(params: QueryParameters, repository: StoreRepository): Observable<String> {
        val userId = params.getParam("userId")
            ?: return Observable.just("userId is null")

        val res = repository.getItems(userId)
        return res.map { it.toString() }
    }
}

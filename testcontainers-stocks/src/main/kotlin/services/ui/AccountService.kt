package services.ui

import config.AccountServiceConfig
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import model.Account
import model.Share
import model.Stock
import services.ExchangeTradeRequest


class AccountService(config: AccountServiceConfig) {
    private val accounts: MutableMap<String, Account> = mutableMapOf()
    private val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }
    private val baseUrl = config.exchangeUrl

    @Synchronized
    fun addAccount(username: String, money: Double) {
        if (accounts.containsKey(username)) error("Username is already taken")
        accounts[username] = Account(username, money, mutableListOf())
    }

    private fun getAccount(username: String) = accounts.getOrElse(username) { error("Account doesn't exist") }

    @Synchronized
    fun addMoney(username: String, amount: Double) {
        val account = getAccount(username)
        account.money += amount
    }

    private fun getStockInfo(name: String): Stock {
        return runBlocking {
            client.get("$baseUrl/info") {
                parameter("stockName", name)
            }
        }
    }

    @Synchronized
    fun getShares(username: String): List<Stock> {
        val account = getAccount(username)
        return account.ownedShares.map { share -> Stock(share.stockName, getStockInfo(share.stockName).price, share.amount) }
    }

    @Synchronized
    fun getTotal(username: String): Double {
        return getShares(username).sumByDouble { it.price * it.amount } + getAccount(username).money
    }

    @Synchronized
    fun buyShares(username: String, stockName: String, amount: Long) {
        val account = getAccount(username)
        val stockInfo = getStockInfo(stockName)
        val price = stockInfo.price * amount
        if (account.money < price) {
            error("Account doesn't have enough money")
        }
        runBlocking {
            client.post<Unit>("$baseUrl/buy") {
                body = defaultSerializer().write(ExchangeTradeRequest(stockName, amount))
            }
        }
        account.money -= price
        val accountShares = account.ownedShares.firstOrNull { it.stockName == stockName }
        if (accountShares == null) {
            account.ownedShares.add(Share(stockName, amount))
        } else {
            accountShares.amount += amount
        }
    }

    @Synchronized
    fun sellShares(username: String, stockName: String, amount: Long) {
        val account = getAccount(username)
        val accountShares = account.ownedShares.firstOrNull { it.stockName == stockName }
        if (accountShares?.amount == null || accountShares.amount < amount) {
            error("Account doesn't have enough shares")
        }
        val stockInfo = getStockInfo(stockName)
        runBlocking {
            client.post<Unit>("$baseUrl/sell") {
                body = defaultSerializer().write(ExchangeTradeRequest(stockName, amount))
            }
        }
        val price = stockInfo.price * amount
        account.money += price
        accountShares.amount -= amount
    }
}
package services.exchange

import model.Stock

class StockExchangeService {
    private val stocks = mutableMapOf<String, Stock>()

    @Synchronized
    fun issueStock(name: String, price: Double, amount: Long) {
        if (stocks.containsKey(name)) error("Stock with name $name already exists")
        stocks[name] = Stock(name, price, amount)
    }

    @Synchronized
    fun getStockInfo(stockName: String): Stock {
        return stocks[stockName] ?: error("Stock $stockName does not exist")
    }

    @Synchronized
    fun buyShares(stockName: String, amount: Long) {
        val stock = stocks[stockName] ?: error("Stock $stockName does not exist")
        if (stock.amount < amount) error("Not enough shares to buy")
        changePrice(stock, amount)
    }

    @Synchronized
    fun sellShares(stockName: String, amount: Long) {
        val stock = stocks[stockName] ?: error("Stock $stockName does not exist")
        changePrice(stock, -amount)
    }

    private fun changePrice(stock: Stock, amountDiff: Long) {
        stock.amount -= amountDiff
        stock.price += amountDiff * stock.price * 0.0375
    }
}
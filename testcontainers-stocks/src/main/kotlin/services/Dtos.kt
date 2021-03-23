package services

import kotlinx.serialization.Serializable

@Serializable
data class IssueRequest(
    val name: String,
    val price: Double,
    val amount: Long
)

@Serializable
data class ExchangeTradeRequest(
    val stockName: String,
    val amount: Long
)

@Serializable
data class AccountTradeRequest(
    val username: String,
    val stockName: String,
    val amount: Long
)

@Serializable
data class RegisterRequest(
    val username: String,
    val money: Double?
)

@Serializable
data class InvestRequest(
    val username: String,
    val money: Double
)
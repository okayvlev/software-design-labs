package model

import kotlinx.serialization.Serializable

@Serializable
data class Stock(
    val name: String,
    var price: Double,
    var amount: Long
)

@Serializable
data class Share(
    val stockName: String,
    var amount: Long
)

@Serializable
data class Account(
    val username: String,
    var money: Double,
    var ownedShares: MutableList<Share>
)
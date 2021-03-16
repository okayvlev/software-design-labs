package model

import kotlinx.serialization.Serializable
import org.bson.Document


@Serializable
data class Item(
    val id: Long,
    val title: String,
    val prices: List<Price>
)

@Serializable
data class Price(
    val value: Double,
    val currency: Currency
)

enum class Currency {
    USD, EUR, RUB
}

@Serializable
data class User(
    val id: String,
    val name: String,
    val currency: Currency
)

fun Document.toUser() = User(
    id = getObjectId("_id").toHexString(),
    name = getString("name"),
    currency = Currency.valueOf(getString("currency"))
)
package model

import kotlinx.serialization.Serializable


@Serializable
data class ItemRequest(
    val title: String,
    val prices: List<Price>
)

@Serializable
data class UserRequest(
    val name: String,
    val currency: Currency
)
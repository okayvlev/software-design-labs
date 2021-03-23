package config

import kotlinx.serialization.Serializable

@Serializable
data class AccountServiceConfig(
    val exchangeUrl: String,
    val port: Int
)
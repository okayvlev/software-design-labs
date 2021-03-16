package configs

import kotlinx.serialization.Serializable

@Serializable
data class ServerConfig(
    val port: Int
)
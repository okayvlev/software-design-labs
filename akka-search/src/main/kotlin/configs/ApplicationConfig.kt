package configs

import kotlinx.serialization.Serializable

@Serializable
data class ApplicationConfig(
    val servers: ServersConfig,
    val service: SearchServiceConfig,
)

@Serializable
data class ServersConfig(
    val googleUrl: String,
    val yandexUrl: String,
    val bingUrl: String,
)

@Serializable
data class SearchServiceConfig(
    val receiveTimeout: Long,
    val responseTimeout: Long,
)
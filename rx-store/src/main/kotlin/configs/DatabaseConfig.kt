package configs

import kotlinx.serialization.Serializable

@Serializable
data class DatabaseConfig(
    val url: String,
    val database: String
)
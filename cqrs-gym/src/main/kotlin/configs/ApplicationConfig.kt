package configs

import kotlinx.serialization.Serializable

@Serializable
data class ApplicationConfig(
    val db: DatabaseConfig,
)

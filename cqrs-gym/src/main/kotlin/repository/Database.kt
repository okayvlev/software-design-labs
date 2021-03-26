package repository

import com.mongodb.client.MongoDatabase
import configs.DatabaseConfig
import org.litote.kmongo.KMongo

open class Database(config: DatabaseConfig) {
    private val client = KMongo.createClient(config.url)
    internal val database: MongoDatabase by lazy { client.getDatabase("gym") }
}
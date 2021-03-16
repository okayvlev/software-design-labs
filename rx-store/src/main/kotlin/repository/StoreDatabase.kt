package repository

import com.mongodb.ConnectionString
import com.mongodb.client.model.Filters
import com.mongodb.rx.client.MongoClients
import com.mongodb.rx.client.MongoDatabase
import com.mongodb.rx.client.Success
import configs.DatabaseConfig
import model.*
import org.bson.Document
import org.bson.types.ObjectId
import rx.Observable
import rx.schedulers.Schedulers


class StoreDatabase(private val config: DatabaseConfig) : StoreRepository {
    private val database: MongoDatabase by lazy { connect() }

    private fun connect(): MongoDatabase {
        val connectionString = ConnectionString(config.url)
        val client = MongoClients.create(connectionString)
        return client.getDatabase(config.database)
    }

    private fun getItems() = database.getCollection("items")
    private fun getUsers() = database.getCollection("users")


    override fun addItem(item: ItemRequest): Observable<Success> {
        if (item.prices.groupBy { it.currency }.any { it.value.size > 1 })
            return Observable.error(IllegalArgumentException("Prices must be specified for each currency at most once"))

        val prices: List<Document> = item.prices.map {
            Document(
                mapOf(
                    "value" to it.value,
                    "currency" to it.currency.name
                )
            )
        }

        return getItems()
            .insertOne(
                Document(
                    mapOf(
                        "title" to item.title,
                        "prices" to prices
                    )
                )
            ).subscribeOn(Schedulers.io())
    }

    override fun registerUser(user: UserRequest): Observable<Success> {
        return getUsers()
            .insertOne(
                Document(
                    mapOf(
                        "name" to user.name,
                        "currency" to user.currency.name
                    )
                )
            ).subscribeOn(Schedulers.io())
    }

    private fun getUser(id: String): Observable<User> {
        return getUsers()
            .find(Filters.eq("_id", ObjectId(id)))
            .toObservable()
            .map(Document::toUser)
            .subscribeOn(Schedulers.io())
    }

    override fun getUserByName(name: String): Observable<User> {
        return getUsers()
            .find(Filters.eq("name", name))
            .toObservable()
            .map(Document::toUser)
            .subscribeOn(Schedulers.io())
    }

    override fun getItems(userId: String): Observable<List<ItemResponse>> {
        val userObservable = getUser(userId)
        return userObservable
            .map { it.currency }
            .flatMap { ccy ->
                getItems()
                    .find()
                    .toObservable()
                    .map { it.toItem(ccy) }
                    .toList()
                    .subscribeOn(Schedulers.io())
            }
    }
}
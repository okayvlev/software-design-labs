package repository

import com.mongodb.rx.client.Success
import model.*
import rx.Observable

interface StoreRepository {
    fun addItem(item: ItemRequest): Observable<Success>
    fun getItems(userId: String): Observable<List<ItemResponse>>
    fun registerUser(user: UserRequest): Observable<Success>
    fun getUserByName(name: String): Observable<User>
}
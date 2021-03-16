package model

import kotlinx.serialization.Serializable
import org.bson.Document


@Serializable
data class ItemResponse(
    val id: String,
    val title: String,
    val price: Double?
)

fun Document.toItem(currency: Currency): ItemResponse {
    val prices = get("prices") as? List<Document>
    val price = prices?.firstOrNull { currency.name.equals(it["currency"].toString(), true) }
    return ItemResponse(
        id = getObjectId("_id").toHexString(),
        title = getString("title"),
        price = price?.getDouble("value")
    )
}
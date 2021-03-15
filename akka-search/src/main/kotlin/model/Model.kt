package model

import kotlinx.serialization.Serializable


@Serializable
data class SearchResult(
    val source: SearchSource,
    val link: String,
    val views: Long,
)

@Serializable
data class SearchResultCollection(
    val list: List<SearchResult>
)

data class SearchQuery(
    val query: String
)

enum class SearchSource {
    Google, Yandex, Bing
}
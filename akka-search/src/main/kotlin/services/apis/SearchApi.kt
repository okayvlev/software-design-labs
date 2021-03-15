package services.apis

import model.SearchResult

interface SearchApi {
    fun getTopResults(query: String): List<SearchResult>
}
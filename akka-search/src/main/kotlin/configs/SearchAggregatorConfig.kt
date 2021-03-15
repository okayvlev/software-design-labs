package configs

import model.SearchSource
import services.apis.SearchApi

data class SearchAggregatorConfig(
    val timeout: Long,
    val serviceMap: Map<SearchSource, SearchApi>
)
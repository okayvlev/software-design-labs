package actors

import akka.actor.UntypedAbstractActor
import model.SearchQuery
import model.SearchResultCollection
import services.apis.SearchApi


open class SearchActor(private val service: SearchApi) : UntypedAbstractActor() {

    override fun onReceive(message: Any) {
        when (message) {
            is SearchQuery -> {
                val results = service
                    .getTopResults(message.query)
                    .take(5)
                context.parent.tell(SearchResultCollection(results), self)
            }
        }
    }
}
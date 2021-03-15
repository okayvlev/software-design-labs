package actors

import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.ReceiveTimeout
import akka.actor.UntypedAbstractActor
import configs.SearchAggregatorConfig
import model.SearchQuery
import model.SearchResult
import model.SearchResultCollection
import model.SearchSource
import java.time.Duration


class SearchAggregatorActor(private val config: SearchAggregatorConfig) : UntypedAbstractActor() {
    private val searchResults: MutableList<SearchResult> = mutableListOf()
    private var requestCounter = 0
    private lateinit var replyTo: ActorRef


    override fun onReceive(message: Any) {
        when (message) {
            is SearchQuery -> sendQuery(message)
            is SearchResultCollection -> processResults(message)
            is ReceiveTimeout -> stop()
        }
    }

    private fun sendQuery(searchQuery: SearchQuery) {
        replyTo = sender
        requestCounter = SearchSource.values().size
        SearchSource.values().forEach { src ->
            val child = createChild(src)
            child.tell(searchQuery, self)
        }
        context.receiveTimeout = Duration.ofMillis(config.timeout)
    }

    private fun createChild(source: SearchSource) =
        context.actorOf(Props.create(SearchActor::class.java, config.serviceMap[source]), source.name)

    private fun processResults(result: SearchResultCollection) {
        searchResults.addAll(result.list)
        requestCounter--
        if (requestCounter == 0) {
            stop()
        }
    }

    private fun stop() {
        replyTo.tell(searchResults, self)
        context.stop(self)
    }

}
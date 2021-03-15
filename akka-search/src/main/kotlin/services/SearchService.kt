package services

import actors.SearchAggregatorActor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.Patterns
import akka.util.Timeout
import configs.SearchAggregatorConfig
import configs.SearchServiceConfig
import configs.ServersConfig
import model.SearchQuery
import model.SearchResult
import model.SearchSource
import org.slf4j.LoggerFactory
import scala.concurrent.Await
import services.apis.BingSearchApi
import services.apis.GoogleSearchApi
import services.apis.YandexSearchApi
import java.time.Duration
import java.util.concurrent.TimeoutException


class SearchService(
    private val serviceConfig: SearchServiceConfig,
    private val serversConfig: ServersConfig
    ) {
    companion object {
        private val system = ActorSystem.create("SearchAggregator")
    }

    private val logger = LoggerFactory.getLogger(SearchService::class.java)
    private val services = with(serversConfig) {
        mapOf(
            SearchSource.Google to GoogleSearchApi(googleUrl),
            SearchSource.Yandex to YandexSearchApi(yandexUrl),
            SearchSource.Bing to BingSearchApi(bingUrl)
        )
    }

    fun search(query: String): List<SearchResult> {
        val masterActor = system.actorOf(
            Props.create(
                SearchAggregatorActor::class.java,
                SearchAggregatorConfig(
                    timeout = serviceConfig.receiveTimeout,
                    serviceMap = services
                )
            )
        )
        val timeout = Timeout.create(Duration.ofMillis(serviceConfig.responseTimeout))
        val future = Patterns.ask(masterActor, SearchQuery(query), timeout)
        val result = try {
            Await.result(future, timeout.duration()) as List<SearchResult>
        } catch (e: TimeoutException) {
            logger.error(e.toString())
            emptyList()
        }
        return result.sortedBy { it.views }
    }
}
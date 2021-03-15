import configs.ApplicationConfig
import configs.loadYaml
import model.SearchSource
import services.SearchService
import services.StubSearchEngineService


fun main() {
    val servers = SearchSource.values().mapIndexed { index, source ->
        StubSearchEngineService(source, 8080 + index)
    }
    servers.forEach { it.start() }

    val config = loadYaml("application.yaml", ApplicationConfig.serializer())

    val service = SearchService(config.service, config.servers)

    while (true) {
        val query = readLine() ?: continue

        val results = service.search(query)
        val maxLength = results.maxOfOrNull { it.link.length } ?: 0
        val resultsBySource = results.groupBy { it.source }

        println("Search results:\n")
        for ((src, results) in resultsBySource) {
            println("$src:")
            results
                .sortedByDescending { it.views }
                .forEach { result ->
                println(String.format("%-${maxLength}s | views: %s", result.link, result.views.toString()))
            }
            println()
        }
    }
}
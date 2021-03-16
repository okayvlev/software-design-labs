import configs.ApplicationConfig
import configs.loadYaml
import repository.StoreDatabase
import services.StoreService


fun main() {
    val config = loadYaml("application.yaml", ApplicationConfig.serializer())
    val database = StoreDatabase(config.db)
    val storeService = StoreService(config.server, database)

    storeService.start()
}
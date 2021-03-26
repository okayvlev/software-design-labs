import configs.ApplicationConfig
import configs.loadYaml
import org.junit.Before
import repository.EventStore
import services.ManagerAdminService
import services.ReportService
import services.TurnstileService

abstract class BaseServiceTests {
    protected val config = loadYaml("application.yaml", ApplicationConfig.serializer())
    protected val eventStore = EventStore(config.db)

    protected lateinit var managerAdminService: ManagerAdminService
    protected lateinit var turnstileService: TurnstileService
    protected lateinit var reportService: ReportService

    @Before
    fun clearEvents() {
        eventStore.database.getCollection("events").drop()
        managerAdminService = ManagerAdminService(config.db)
        turnstileService = TurnstileService(config.db)
        reportService = ReportService(config.db)
    }
}
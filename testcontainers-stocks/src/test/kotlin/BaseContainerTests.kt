import config.AccountServiceConfig
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import model.Stock
import org.junit.After
import org.junit.Before
import org.testcontainers.containers.FixedHostPortGenericContainer
import services.ui.AccountService


class StockExchangeContainer(image: String) : FixedHostPortGenericContainer<StockExchangeContainer>(image)


abstract class BaseContainerTests {
    private val stockExchangeContainer = StockExchangeContainer("stocks:latest")
        .withFixedExposedPort(8080, 8080)
        .withExposedPorts(8080)

    private val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    private val asConfig = AccountServiceConfig(
        exchangeUrl = "http://localhost:8080",
        port = 80
    )

    internal lateinit var accountService: AccountService

    @Before
    fun beforeEach() {
        stockExchangeContainer.start()
        initStocks()
        accountService = AccountService(asConfig)
    }

    @After
    fun afterEach() {
        stockExchangeContainer.stop()
    }

    private fun initStocks() {
        runBlocking {
            for (stock in stocks) {
                client.post<Unit>("${asConfig.exchangeUrl}/issue") {
                    body = defaultSerializer().write(stock)
                }
            }
        }
    }

    private val stocks = listOf(
        Stock("MSFT", 1.2, 30),
        Stock("GOOG", 2.1, 10),
        Stock("YNDX", 3.5, 12)
    )
}
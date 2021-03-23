import junit.framework.Assert.assertEquals
import model.Stock
import org.junit.Test


class FunctionalTests : BaseContainerTests() {

    @Test
    fun `create account`() {
        accountService.addAccount("john", 1.0)
        accountService.addAccount("mary", 0.0)
    }

    @Test
    fun `get total after investment`() {
        accountService.addAccount("john", 1.0)
        accountService.addAccount("mary", 2.0)

        accountService.addMoney("john", 5.0)

        assertEquals(6.0, accountService.getTotal("john"))
        assertEquals(2.0, accountService.getTotal("mary"))
    }

    @Test
    fun `buy shares`() {
        accountService.addAccount("john", 1.0)

        accountService.addMoney("john", 500.0)
        accountService.buyShares("john", "MSFT", 3)
        accountService.buyShares("john", "GOOG", 5)

        assertEquals(listOf(Stock("MSFT", 1.335, 3), Stock("GOOG", 2.49375, 5)), accountService.getShares("john"))
        assertEquals(503.37375, accountService.getTotal("john"), 1e-5)
    }

    @Test
    fun `sell shares`() {
        accountService.addAccount("mary", 1.0)

        accountService.addMoney("mary", 500.0)
        accountService.buyShares("mary", "MSFT", 3)
        accountService.buyShares("mary", "GOOG", 5)
        accountService.sellShares("mary", "GOOG", 1)

        assertEquals(listOf(Stock("MSFT", 1.335, 3), Stock("GOOG", 2.400234375, 4)), accountService.getShares("mary"))
        assertEquals(502.999687, accountService.getTotal("mary"), 1e-5)
    }
}
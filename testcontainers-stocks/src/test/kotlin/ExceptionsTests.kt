import org.junit.Test

class ExceptionsTests : BaseContainerTests() {
    @Test(expected = IllegalStateException::class)
    fun `create accounts with one username`() {
        accountService.addAccount("john", 1.0)
        accountService.addAccount("john", 0.0)
    }

    @Test(expected = IllegalStateException::class)
    fun `access invalid account`() {
        accountService.addAccount("john", 1.0)
        accountService.addAccount("mary", 2.0)

        accountService.addMoney("john", 5.0)

        accountService.getTotal("johnny")
    }

    @Test(expected = IllegalStateException::class)
    fun `buy shares with not enough money`() {
        accountService.addAccount("john", 1.0)

        accountService.buyShares("john", "MSFT", 3)
    }

    @Test(expected = IllegalStateException::class)
    fun `buy too many shares`() {
        accountService.addAccount("john", 1000.0)

        accountService.buyShares("john", "MSFT", 31)
    }

    @Test(expected = IllegalStateException::class)
    fun `sell extra shares`() {
        accountService.addAccount("mary", 1.0)

        accountService.addMoney("mary", 500.0)
        accountService.buyShares("mary", "MSFT", 3)
        accountService.buyShares("mary", "GOOG", 5)
        accountService.sellShares("mary", "GOOG", 7)
    }
}
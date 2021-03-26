import model.Direction
import org.junit.Test
import java.time.LocalDateTime

class QueriesExceptionsTests : BaseServiceTests() {

    @Test(expected = IllegalStateException::class)
    fun `check membership of invalid member`() {
        val now = LocalDateTime.now()
        turnstileService.checkMembership(0, now.plusDays(1))
    }

    @Test(expected = IllegalStateException::class)
    fun `get info of invalid member`() {
        managerAdminService.getInfo(0)
    }

    @Test(expected = IllegalStateException::class)
    fun `member exited before entering`() {
        val now = LocalDateTime.now()
        val johnId = managerAdminService.addMember("John", now)
        turnstileService.pass(johnId, Direction.Out, now)
        turnstileService.pass(johnId, Direction.In, now)

        reportService.start()
        reportService.getStatistics(johnId)
    }

}
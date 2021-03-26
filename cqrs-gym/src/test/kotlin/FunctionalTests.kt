import infra.commands.PassCommand
import model.Direction
import model.Direction.In
import model.Direction.Out
import model.Statistics
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime


class FunctionalTests : BaseServiceTests() {

    @Test
    fun `checking memberships correctly`() {
        val now = LocalDateTime.now()
        val johnId = managerAdminService.addMember("John", now)
        val maryId = managerAdminService.addMember("Mary", now)
        managerAdminService.extendMembership(johnId, 10)
        managerAdminService.extendMembership(maryId, 20)

        val johnIsMember1Day = turnstileService.checkMembership(johnId, now.plusDays(1))
        val johnIsMember15Days = turnstileService.checkMembership(johnId, now.plusDays(15))
        val maryIsMember1Day = turnstileService.checkMembership(maryId, now.plusDays(1))
        val maryIsMember15Days = turnstileService.checkMembership(maryId, now.plusDays(15))

        assertTrue(johnIsMember1Day)
        assertTrue(maryIsMember1Day)
        assertFalse(johnIsMember15Days)
        assertTrue(maryIsMember15Days)
    }

    @Test
    fun `loading stats correctly`() {
        val now = LocalDateTime.now()
        val johnId = managerAdminService.addMember("John", now)
        val maryId = managerAdminService.addMember("Mary", now)
        val bobId = managerAdminService.addMember("Bob", now)
        managerAdminService.extendMembership(johnId, 10)
        managerAdminService.extendMembership(maryId, 20)
        managerAdminService.extendMembership(maryId, 5)

        turnstileService.pass(johnId, In, now.plusDays(1))
        turnstileService.pass(johnId, Out, now.plusDays(1).plusHours(2))
        turnstileService.pass(maryId, In, now.plusHours(1))
        turnstileService.pass(maryId, Out, now.plusHours(3))
        turnstileService.pass(johnId, In, now.plusDays(2))
        turnstileService.pass(johnId, Out, now.plusDays(2).plusHours(1))
        turnstileService.pass(johnId, In, now.plusDays(4))
        turnstileService.pass(johnId, Out, now.plusDays(4).plusHours(1))

        reportService.start()
        val johnStats = reportService.getStatistics(johnId)
        val maryStats = reportService.getStatistics(maryId)
        val bobStats = reportService.getStatistics(bobId)

        assertEquals(Statistics(johnId, listOf(now.plusDays(1), now.plusDays(2), now.plusDays(4)), 1.5, 80.0), johnStats)
        assertEquals(Statistics(maryId, listOf(now.plusHours(1)), 0.0, 120.0), maryStats)
        assertEquals(Statistics(bobId, emptyList(), 0.0, 0.0), bobStats)
    }


    @Test
    fun `gathering stats correctly`() {
        val now = LocalDateTime.now()
        reportService.start()

        fun passCommand(memberId: Long, direction: Direction, now: LocalDateTime) {
            turnstileService.pass(memberId, direction, now)
            reportService.processCommand(PassCommand(memberId, direction, now))
        }

        val johnId = managerAdminService.addMember("John", now)
        val maryId = managerAdminService.addMember("Mary", now)
        val bobId = managerAdminService.addMember("Bob", now)
        managerAdminService.extendMembership(johnId, 10)
        managerAdminService.extendMembership(maryId, 20)
        managerAdminService.extendMembership(maryId, 5)

        passCommand(johnId, In, now.plusDays(1))
        passCommand(johnId, Out, now.plusDays(1).plusHours(2))
        passCommand(maryId, In, now.plusHours(1))
        passCommand(maryId, Out, now.plusHours(3))
        passCommand(johnId, In, now.plusDays(2))
        passCommand(johnId, Out, now.plusDays(2).plusHours(1))
        passCommand(johnId, In, now.plusDays(4))
        passCommand(johnId, Out, now.plusDays(4).plusHours(1))

        val johnStats = reportService.getStatistics(johnId)
        val maryStats = reportService.getStatistics(maryId)
        val bobStats = reportService.getStatistics(bobId)

        assertEquals(Statistics(johnId, listOf(now.plusDays(1), now.plusDays(2), now.plusDays(4)), 1.5, 80.0), johnStats)
        assertEquals(Statistics(maryId, listOf(now.plusHours(1)), 0.0, 120.0), maryStats)
        assertEquals(Statistics(bobId, emptyList(), 0.0, 0.0), bobStats)
    }
}
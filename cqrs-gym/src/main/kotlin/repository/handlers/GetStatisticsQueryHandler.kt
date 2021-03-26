package repository.handlers

import infra.commands.Command
import infra.commands.PassCommand
import infra.queries.GetStatisticsQuery
import model.Statistics
import model.Direction.In
import model.Direction.Out
import repository.EventStore
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class GetStatisticsQueryHandler : QueryHandler<GetStatisticsQuery, Statistics> {
    private val statsMap: MutableMap<Long, StatsObject> = mutableMapOf()

    fun init(eventStore: EventStore) {
        eventStore.findCommands("pass").forEach(::processCommand)
    }

    fun processCommand(command: Command) {
        when {
            command is PassCommand && command.direction == In -> {
                val stats = statsMap.getOrPut(command.memberId, ::emptyStats)
                if (stats.attendance.isNotEmpty()) {
                    val lastEntrance = stats.attendance.last()
                    stats.sumFrequencies += lastEntrance.until(command.timestamp, ChronoUnit.DAYS)
                }
                stats.attendance.add(command.timestamp)
            }
            command is PassCommand && command.direction == Out -> {
                val stats = statsMap.getOrPut(command.memberId, ::emptyStats)
                val lastEntrance = stats.attendance.lastOrNull() ?: error("Exit event encountered before first entrance")
                val curDuration = lastEntrance.until(command.timestamp, ChronoUnit.MINUTES)
                stats.sumDurations += curDuration
            }
        }
    }

    override fun execute(query: GetStatisticsQuery): Statistics {
        val stats = statsMap.getOrDefault(query.memberId, emptyStats())
        with(stats) {
            return Statistics(
                memberId = query.memberId,
                attendance = attendance,
                averageFrequency = getAverageFromSum(sumFrequencies, attendance.size - 1),
                averageDuration = getAverageFromSum(sumDurations, attendance.size),
            )
        }
    }

    private fun getAverageFromSum(sum: Long, count: Int): Double =
        if (count <= 0)
            0.0
        else
            sum.toDouble() / count

    private data class StatsObject(
        val attendance: MutableList<LocalDateTime>,
        var sumDurations: Long,
        var sumFrequencies: Long
    )

    private fun emptyStats() = StatsObject(mutableListOf(), 0, 0)
}
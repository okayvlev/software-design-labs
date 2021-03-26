package services

import configs.DatabaseConfig
import infra.commands.Command
import infra.queries.GetStatisticsQuery
import model.Statistics
import repository.EventStore
import repository.handlers.GetStatisticsQueryHandler

class ReportService(eventStoreConfig: DatabaseConfig) {
    private val eventStore = EventStore(eventStoreConfig)
    private val queryHandler = GetStatisticsQueryHandler()

    fun start() {
        queryHandler.init(eventStore)
    }

    fun getStatistics(memberId: Long): Statistics {
        return queryHandler.execute(GetStatisticsQuery(memberId))
    }

    fun processCommand(command: Command) {
        queryHandler.processCommand(command)
    }
}
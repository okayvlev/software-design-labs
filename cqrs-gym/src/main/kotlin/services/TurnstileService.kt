package services

import configs.DatabaseConfig
import infra.commands.PassCommand
import infra.queries.CheckMembershipQuery
import model.Direction
import repository.EventStore
import repository.handlers.CheckMembershipHandler
import java.time.LocalDateTime

class TurnstileService(eventStoreConfig: DatabaseConfig) {
    private val eventStore = EventStore(eventStoreConfig)
    private val queryHandler = CheckMembershipHandler(eventStore)

    fun checkMembership(memberId: Long, now: LocalDateTime): Boolean {
        return queryHandler.execute(CheckMembershipQuery(memberId, now))
    }

    fun pass(memberId: Long, direction: Direction, now: LocalDateTime) {
        eventStore.storeCommand(PassCommand(memberId, direction, now))
    }
}
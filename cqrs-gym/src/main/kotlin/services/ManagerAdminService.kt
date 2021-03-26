package services

import configs.DatabaseConfig
import infra.commands.AddMemberCommand
import infra.commands.ExtendMembershipCommand
import infra.queries.GetMemberInfoQuery
import model.Member
import repository.EventStore
import repository.handlers.GetMemberInfoQueryHandler
import java.time.LocalDateTime
import kotlin.random.Random


class ManagerAdminService(eventStoreConfig: DatabaseConfig) {
    private val eventStore = EventStore(eventStoreConfig)
    private val queryHandler = GetMemberInfoQueryHandler(eventStore)

    fun addMember(name: String, now: LocalDateTime): Long {
        val id = Random.nextLong()
        eventStore.storeCommand(AddMemberCommand(id, name, now))
        return id
    }

    fun extendMembership(memberId: Long, days: Int) {
        eventStore.storeCommand(ExtendMembershipCommand(memberId, days))
    }

    fun getInfo(memberId: Long): Member {
        return queryHandler.execute(GetMemberInfoQuery(memberId))
    }
}
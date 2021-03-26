package repository.handlers

import com.mongodb.client.model.Filters.*
import infra.commands.AddMemberCommand
import infra.commands.ExtendMembershipCommand
import infra.queries.CheckMembershipQuery
import repository.EventStore

class CheckMembershipHandler(private val eventStore: EventStore) : QueryHandler<CheckMembershipQuery, Boolean> {
    override fun execute(query: CheckMembershipQuery): Boolean {
        val filters = or(
            and(
                eq("type", "addMember"),
                eq("id", query.memberId)
            ),
            and(
                eq("type", "extendMembership"),
                eq("memberId", query.memberId)
            )
        )
        val commands = eventStore.findCommands(filters)
        val registrationCommand = commands.firstOrNull() as? AddMemberCommand
            ?: error("Member not registered")
        val extensionCommands = commands.drop(1).map {
            it as? ExtendMembershipCommand ?: error("Incorrect user records sequence")
        }
        val membershipEndTs = extensionCommands.fold(registrationCommand.timestamp) { curTs, cmd ->
            curTs.plusDays(cmd.days.toLong())
        }
        return query.now < membershipEndTs
    }
}
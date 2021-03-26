package repository.handlers

import com.mongodb.client.model.Filters.*
import infra.commands.AddMemberCommand
import infra.queries.GetMemberInfoQuery
import model.Member
import repository.EventStore

class GetMemberInfoQueryHandler(private val eventStore: EventStore) : QueryHandler<GetMemberInfoQuery, Member> {
    override fun execute(query: GetMemberInfoQuery): Member {
        val filters = and(eq("type", "addMember"), eq("id", query.memberId))
        val command = eventStore.findCommands(filters).firstOrNull() as? AddMemberCommand
            ?: error("Member not registered")
        return Member(command.id, command.name, command.timestamp)
    }
}
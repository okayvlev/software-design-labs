package infra.commands

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
@SerialName("addMember")
data class AddMemberCommand(val id: Long, val name: String, @Contextual val timestamp: LocalDateTime) : Command

@Serializable
@SerialName("extendMembership")
data class ExtendMembershipCommand(val memberId: Long, val days: Int) : Command

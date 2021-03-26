package infra.commands

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import model.Direction
import java.time.LocalDateTime

@Serializable
@SerialName("pass")
data class PassCommand(val memberId: Long, val direction: Direction, @Contextual val timestamp: LocalDateTime) : Command


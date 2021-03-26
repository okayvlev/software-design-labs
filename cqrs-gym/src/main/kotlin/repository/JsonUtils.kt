package repository

import infra.commands.AddMemberCommand
import infra.commands.Command
import infra.commands.ExtendMembershipCommand
import infra.commands.PassCommand
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import java.time.LocalDateTime

@Serializer(forClass = LocalDateTime::class)
object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString())
    }
}

val jsonCommandsSerializer = Json {
    serializersModule = SerializersModule {
        polymorphic(Command::class) {
            subclass(AddMemberCommand::class, AddMemberCommand.serializer())
            subclass(ExtendMembershipCommand::class, ExtendMembershipCommand.serializer())
            subclass(PassCommand::class, PassCommand.serializer())
        }
        this.contextual(LocalDateTime::class, LocalDateTimeSerializer)
    }
    ignoreUnknownKeys = true
}
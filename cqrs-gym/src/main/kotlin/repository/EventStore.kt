package repository

import com.mongodb.client.model.Filters
import configs.DatabaseConfig
import infra.commands.Command
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.bson.Document
import org.bson.conversions.Bson


class EventStore(config: DatabaseConfig) : Database(config) {
    private val serializer = jsonCommandsSerializer
    private val events = database.getCollection("events")

    private fun Command.toDocument(): Document =
        Document.parse(serializer.encodeToString(this))

    private fun Document.toCommand(): Command =
        serializer.decodeFromString(this.toJson())

    fun storeCommand(command: Command) {
        events.insertOne(command.toDocument())
    }

    fun findCommands(type: String): List<Command> {
        return findCommands(Filters.eq("type", type))
    }

    fun findCommands(filters: Bson): List<Command> {
        return events.find(filters).toList().map { it.toCommand() }
    }
}
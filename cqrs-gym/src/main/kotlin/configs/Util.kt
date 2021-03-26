package configs

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.DeserializationStrategy
import java.io.File

fun <T> loadYaml(path: String, serializer: DeserializationStrategy<T>): T {
    val file = File(path)
    return Yaml.default.decodeFromString(serializer, file.readText())
}
package ndy.plugins

import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent
import io.ktor.serialization.ContentConverter
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.util.reflect.TypeInfo
import io.ktor.utils.io.ByteReadChannel

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

/**
 * configure ContentNegotiation with json-kotlinx
 *
 * reference - https://ktor.io/docs/serialization.html
 */
@OptIn(ExperimentalSerializationApi::class) /* for the usage of explicitNull flag */
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        val serializer = KotlinxSerializationConverter(Json { explicitNulls = true })
        val deserializer = KotlinxSerializationConverter(Json { explicitNulls = false })

        val myConverter = MyConverter(serializer, deserializer)
        register(ContentType.Application.Json, myConverter)
    }
}

/**
 * custom converter that contains delegate for
 * serialization & deserialization each
 */
class MyConverter(
    private val serializer: ContentConverter,
    private val deserializer: ContentConverter
) : ContentConverter {

    override suspend fun serializeNullable(
        contentType: ContentType,
        charset: io.ktor.utils.io.charsets.Charset,
        typeInfo: TypeInfo,
        value: Any?
    ): OutgoingContent? {
        return serializer.serializeNullable(contentType, charset, typeInfo, value)
    }

    override suspend fun deserialize(
        charset: io.ktor.utils.io.charsets.Charset,
        typeInfo: TypeInfo,
        content: ByteReadChannel
    ): Any? {
        return deserializer.deserialize(charset, typeInfo, content)
    }
}

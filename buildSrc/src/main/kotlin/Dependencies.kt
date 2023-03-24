import kotlin.reflect.full.declaredMemberProperties

object Versions {
    const val ktorVersion = "2.2.4"
    const val kotlinVersion = "1.8.10"
    const val logbackVersion = "1.2.11"
    const val exposedVersion = "0.41.1"
    const val h2Version = "2.1.214"
    const val koinVersion = "3.3.0"
}

sealed class Dependencies {

    fun list() = this::class.declaredMemberProperties.map { it.getter.call().toString() }

    object Ktor : Dependencies() {
        // core
        const val SERVER_CORE = "io.ktor:ktor-server-core-jvm:${Versions.ktorVersion}"
        const val SERVER_TEST = "io.ktor:ktor-server-tests-jvm:${Versions.ktorVersion}"

        // auth
        const val SERVER_AUTH = "io.ktor:ktor-server-auth-jvm:${Versions.ktorVersion}"
        const val SERVER_AUTH_JWT = "io.ktor:ktor-server-auth-jwt-jvm:${Versions.ktorVersion}"
        const val SERVER_CORS = "io.ktor:ktor-server-cors-jvm:${Versions.ktorVersion}"

        // wheb
        const val KTOR_SERVER_NETTY = "io.ktor:ktor-server-netty-jvm:${Versions.ktorVersion}"
        const val KTOR_SERVER_CONTENT = "io.ktor:ktor-server-content-negotiation-jvm:${Versions.ktorVersion}"
        const val KTOR_SERVER_SERIALIZATION = "io.ktor:ktor-serialization-kotlinx-json-jvm:${Versions.ktorVersion}"
    }

    object Persistence : Dependencies() {
        const val EXPOSED_CORE = "org.jetbrains.exposed:exposed-core:${Versions.exposedVersion}"
        const val EXPOSED_JDBC = "org.jetbrains.exposed:exposed-jdbc:${Versions.exposedVersion}"
        const val H2_DATABASE = "com.h2database:h2:${Versions.h2Version}"
    }

    object Logging : Dependencies() {
        const val LOGBACK = "ch.qos.logback:logback-classic:${Versions.logbackVersion}"
    }

    object KOIN : Dependencies() {
        const val KOIN_KTOR = "io.insert-koin:koin-ktor:${Versions.koinVersion}"
        const val KOIN_LOGGER = "io.insert-koin:koin-logger-slf4j:${Versions.koinVersion}"
        const val KOIN_TEST = "io.insert-koin:koin-test:${Versions.koinVersion}"
    }
}


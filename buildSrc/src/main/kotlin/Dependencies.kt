import kotlin.reflect.full.declaredMemberProperties

object Versions {
    const val ktorVersion = "2.2.4"
    const val exposedVersion = "0.41.1"
    const val h2Version = "2.1.214"
    const val koinVersion = "3.3.0"
    const val kotestVersion = "5.5.5"
    const val kotestKtorVersion = "2.0.0"
    const val kotestKoinVersion = "1.1.0"
}

sealed class Dependencies {

    fun map() = this::class.declaredMemberProperties.map {
        it.name to it.getter.call().toString()
    }.groupBy({ it.first }, { it.second }).mapValues { it.value[0] }

    object Ktor : Dependencies() {
        // core
        const val SERVER_CORE = "io.ktor:ktor-server-core-jvm:${Versions.ktorVersion}"
        const val SERVER_TEST = "io.ktor:ktor-server-tests-jvm:${Versions.ktorVersion}"

        // server
        const val SERVER_NETTY = "io.ktor:ktor-server-netty-jvm:${Versions.ktorVersion}"
        const val SERVER_CONTENT = "io.ktor:ktor-server-content-negotiation-jvm:${Versions.ktorVersion}"
        const val SERVER_SERIALIZATION = "io.ktor:ktor-serialization-kotlinx-json-jvm:${Versions.ktorVersion}"

        // server-auth
        const val SERVER_AUTH = "io.ktor:ktor-server-auth-jvm:${Versions.ktorVersion}"
        const val SERVER_AUTH_JWT = "io.ktor:ktor-server-auth-jwt-jvm:${Versions.ktorVersion}"
        const val SERVER_CORS = "io.ktor:ktor-server-cors-jvm:${Versions.ktorVersion}"

        // client
        const val CLIENT_CONTENT_TEST = "io.ktor:ktor-client-content-negotiation-jvm:${Versions.ktorVersion}" // test
    }

    object Persistence : Dependencies() {
        const val EXPOSED_CORE = "org.jetbrains.exposed:exposed-core:${Versions.exposedVersion}"
        const val EXPOSED_JDBC = "org.jetbrains.exposed:exposed-jdbc:${Versions.exposedVersion}"
        const val H2_DATABASE = "com.h2database:h2:${Versions.h2Version}"
    }


    object KOIN : Dependencies() {
        const val KTOR = "io.insert-koin:koin-ktor:${Versions.koinVersion}"
        const val LOGGER_SLF4J = "io.insert-koin:koin-logger-slf4j:${Versions.koinVersion}"
        const val TEST = "io.insert-koin:koin-test:${Versions.koinVersion}"
    }

    object KOTEST : Dependencies() {
        const val RUNNER = "io.kotest:kotest-runner-junit5:${Versions.kotestVersion}"
        const val ASSERTIONS_CORE = "io.kotest:kotest-assertions-core:${Versions.kotestVersion}"
        const val ASSERTIONS_KTOR = "io.kotest.extensions:kotest-assertions-ktor:${Versions.kotestKtorVersion}"
        const val PROPERTY = "io.kotest:kotest-property:${Versions.kotestVersion}"
        const val EXTENSIONS_KOIN = "io.kotest.extensions:kotest-extensions-koin:${Versions.kotestKoinVersion}"
    }

    object ETC : Dependencies() {
        const val KTOR_ENV_CONFIG = "de.sharpmind.ktor:ktor-env-config:2.0.1" // properties
        const val LOGBACK_CLASSIC = "ch.qos.logback:logback-classic:1.2.11" //logging
        const val JBCrypt = "org.mindrot:jbcrypt:0.4" //security
        const val KONFORM = "io.konform:konform:0.4.0" // validation
    }
}


import kotlin.reflect.full.declaredMemberProperties

object Versions {
    const val ktorVersion = "2.2.4"
    const val kotlinVersion = "1.8.10"
    const val logbackVersion = "1.2.11"
    const val exposedVersion = "0.41.1"
    const val h2Version = "2.1.214"
    const val koinVersion = "3.3.0"
    const val kotestVersion = "5.5.5"
    const val kotestKtorVersion = "2.0.0"
}

sealed class Dependencies {

    fun map() = this::class.declaredMemberProperties.map {
        it.name to it.getter.call().toString()
    }.groupBy({ it.first }, { it.second }).mapValues { it.value[0] }

    object Ktor : Dependencies() {
        // core
        const val SERVER_CORE = "io.ktor:ktor-server-core-jvm:${Versions.ktorVersion}"
        const val SERVER_TEST = "io.ktor:ktor-server-tests-jvm:${Versions.ktorVersion}"

        // auth
        const val SERVER_AUTH = "io.ktor:ktor-server-auth-jvm:${Versions.ktorVersion}"
        const val SERVER_AUTH_JWT = "io.ktor:ktor-server-auth-jwt-jvm:${Versions.ktorVersion}"
        const val SERVER_CORS = "io.ktor:ktor-server-cors-jvm:${Versions.ktorVersion}"

        // server
        const val KTOR_SERVER_NETTY = "io.ktor:ktor-server-netty-jvm:${Versions.ktorVersion}"
        const val KTOR_SERVER_CONTENT = "io.ktor:ktor-server-content-negotiation-jvm:${Versions.ktorVersion}"
        const val KTOR_SERVER_SERIALIZATION = "io.ktor:ktor-serialization-kotlinx-json-jvm:${Versions.ktorVersion}"

        // client
        const val KTOR_CLIENT_CONTENT_TEST = "io.ktor:ktor-client-content-negotiation-jvm:${Versions.ktorVersion}" // test
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

    object KOTEST : Dependencies() {
        const val KOTEST_RUNNER = "io.kotest:kotest-runner-junit5:${Versions.kotestVersion}"
        const val KOTEST_ASSERTIONS = "io.kotest:kotest-assertions-core:${Versions.kotestVersion}"
        const val KOTEST_KTOR = "io.kotest.extensions:kotest-assertions-ktor:${Versions.kotestKtorVersion}"
        const val KOTEST_PROPERTY = "io.kotest:kotest-property:${Versions.kotestVersion}"
    }
}


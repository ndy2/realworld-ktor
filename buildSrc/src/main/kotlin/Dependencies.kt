import kotlin.reflect.full.declaredMemberProperties

object Versions {
    const val ktorVersion = "2.2.4"
    const val exposedVersion = "0.41.1"
    const val h2Version = "2.1.214"
    const val koinVersion = "3.3.0"
    const val kotestVersion = "5.5.5"
}

sealed class Dependencies {

    fun map() = this::class.declaredMemberProperties.map {
        it.name to it.getter.call().toString()
    }.groupBy({ it.first }, { it.second }).mapValues { it.value[0] }

    //https://ktor.io/
    object Ktor : Dependencies() {
        // core
        const val SERVER_CORE = "io.ktor:ktor-server-core-jvm:${Versions.ktorVersion}"
        const val SERVER_TEST = "io.ktor:ktor-server-tests-jvm:${Versions.ktorVersion}"

        // server
        const val SERVER_NETTY = "io.ktor:ktor-server-netty-jvm:${Versions.ktorVersion}"
        const val SERVER_CONTENT = "io.ktor:ktor-server-content-negotiation-jvm:${Versions.ktorVersion}"
        const val SERVER_SERIALIZATION = "io.ktor:ktor-serialization-kotlinx-json-jvm:${Versions.ktorVersion}"
        const val SERVER_STATUS_PAGES = "io.ktor:ktor-server-status-pages:${Versions.ktorVersion}"
        const val SERVER_SEROUCES = "io.ktor:ktor-server-resources:${Versions.ktorVersion}"

        // server-auth
        const val SERVER_AUTH = "io.ktor:ktor-server-auth-jvm:${Versions.ktorVersion}"
        const val SERVER_AUTH_JWT = "io.ktor:ktor-server-auth-jwt-jvm:${Versions.ktorVersion}"
        const val SERVER_CORS = "io.ktor:ktor-server-cors-jvm:${Versions.ktorVersion}"

        // client - used in test
        const val CLIENT_CONTENT_TEST = "io.ktor:ktor-client-content-negotiation-jvm:${Versions.ktorVersion}"
        const val CLIENT_RESOURCES_TEST = "io.ktor:ktor-client-resources:${Versions.ktorVersion}"
    }

    object Persistence : Dependencies() {
        //https://github.com/JetBrains/Exposed
        const val EXPOSED_CORE = "org.jetbrains.exposed:exposed-core:${Versions.exposedVersion}"
        const val EXPOSED_JDBC = "org.jetbrains.exposed:exposed-jdbc:${Versions.exposedVersion}"
        const val EXPOSED_KOTLIN_DATETIME = "org.jetbrains.exposed:exposed-kotlin-datetime:${Versions.exposedVersion}"

        //https://www.h2database.com/
        const val H2_DATABASE = "com.h2database:h2:${Versions.h2Version}"
    }


    //https://insert-koin.io/
    object KOIN : Dependencies() {
        const val KTOR = "io.insert-koin:koin-ktor:${Versions.koinVersion}"
        const val LOGGER_SLF4J = "io.insert-koin:koin-logger-slf4j:${Versions.koinVersion}"
        const val TEST = "io.insert-koin:koin-test:${Versions.koinVersion}"
    }

    //https://kotest.io/
    object KOTEST : Dependencies() {
        const val RUNNER = "io.kotest:kotest-runner-junit5:${Versions.kotestVersion}"

        //https://kotest.io/docs/assertions/assertions.html
        const val ASSERTIONS_CORE = "io.kotest:kotest-assertions-core:${Versions.kotestVersion}"

        //https://kotest.io/docs/proptest/property-based-testing.html
        const val PROPERTY = "io.kotest:kotest-property:${Versions.kotestVersion}"

        //https://kotest.io/docs/assertions/ktor-matchers.html
        const val ASSERTIONS_KTOR = "io.kotest.extensions:kotest-assertions-ktor:2.0.0"

        //https://kotest.io/docs/assertions/konform-matchers.html
        const val EXTENSIONS_KONFORM = "io.kotest.extensions:kotest-assertions-konform:1.0.2"
    }

    object ETC : Dependencies() {
        //https://github.com/sharpmind-de/ktor-env-config
        const val KTOR_ENV_CONFIG = "de.sharpmind.ktor:ktor-env-config:2.0.1" // properties

        //https://github.com/qos-ch/logback
        const val LOGBACK_CLASSIC = "ch.qos.logback:logback-classic:1.2.11" // logging

        //https://github.com/jeremyh/jBCrypt
        const val JBCrypt = "org.mindrot:jbcrypt:0.4" // security

        //https://github.com/konform-kt/konform
        const val KONFORM = "io.konform:konform:0.4.0" // validation

        //https://github.com/Kotlin/kotlinx-datetime
        const val KOTLINX_DATETIME = "org.jetbrains.kotlinx:kotlinx-datetime:0.4.0" // serialize date and time
    }
}


plugins {
    kotlin("jvm") version "1.8.10"
    id("io.ktor.plugin") version "2.2.4"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
    id("io.kotest") version "0.4.10"
    jacoco
}

group = "ndy"
version = "0.0.1"
application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

//https://youtu.be/GISPalIVdQY
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport")
}

// ref @https://stackoverflow.com/questions/29887805/filter-jacoco-coverage-reports-with-gradle
tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        html.required.set(true)
        csv.required.set(true) // for badge @README.md
        xml.required.set(false)
    }

    classDirectories.setFrom(files(classDirectories.files.map {
        fileTree(it).apply {
            exclude(
                "ndy/ApplicationKt.class",
                "ndy/context/**/*.*",
                "ndy/exception/**/*.*",
                "ndy/plugins/**/*.*",
                "ndy/util/**/*.*",
                "ndy/resources/**/*.*",
                "ndy/**/*Request*.*",
                "ndy/**/*Response*.*",
                "ndy/**/*Result.*",
            )
        }
    }))
    doLast {
        println("file://${project.rootDir}/build/reports/jacoco/test/html/index.html")
    }
}

//see buildSrc/src/main/kotlin/Dependencies
dependencies {
    applyAll()
}

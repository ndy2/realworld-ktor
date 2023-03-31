import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.applyAll() {
    Dependencies::class
        .sealedSubclasses
        .forEach { applyDependencies(it.objectInstance!!) }
}

// apply `implementation` by default
// if the name of the constant or value contains "test" then apply `testImplementation`
fun DependencyHandler.applyDependencies(dependencies: Dependencies) {

    dependencies.map().forEach { key, value ->
        val isTestDependency = value.contains("test", true) || key.contains("test", true)
        if (isTestDependency) add("testImplementation", value)
        else add("implementation", value)
    }
}

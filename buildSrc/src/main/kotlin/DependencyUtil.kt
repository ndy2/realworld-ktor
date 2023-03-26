import org.gradle.api.artifacts.dsl.DependencyHandler

// Dependencies 하위 클래스의 모든 Sealed Sub classes 에 대해 applyDependencies 적용
fun DependencyHandler.applyAll() {
    Dependencies::class
        .sealedSubclasses
        .forEach { applyDependencies(it.objectInstance!!) }
}

// 기본적으로 implementation 적용, 이름에 test 가 있는 경우 testImplementation 적용
fun DependencyHandler.applyDependencies(dependencies: Dependencies) {

    dependencies.map().forEach { key, value ->
        val isTestDependency = value.contains("test", true) || key.contains("test", true)
        if (isTestDependency) add("testImplementation", value)
        else add("implementation", value)
    }
}

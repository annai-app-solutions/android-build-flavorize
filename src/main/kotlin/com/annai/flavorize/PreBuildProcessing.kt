package com.annai.flavorize

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

// ✅ Pre-Build Task
abstract class PreBuildProcessingTask : DefaultTask() {

    @Internal // 🔹 Tells Gradle to ignore this field for up-to-date checks
    var variantName: String = "Unknown-Unknown"

    init {
        group = "build"
        description = "Pre-build processing task"
    }

    @TaskAction
    fun executePreBuild() {
        val (flavor, buildType) = variantName.split("-")
        println("⚙️ Pre-Build Processing: Flavor = $flavor, Build Type = $buildType")
    }
}

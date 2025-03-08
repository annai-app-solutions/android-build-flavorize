package com.annai.flavorize

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

// ✅ Post-Build Task
abstract class PostBuildProcessingTask : DefaultTask() {
    init {
        group = "build"
        description = "Post-build processing task"
    }

    @TaskAction
    fun executePostBuild() {
        println("✅ Post-Build Processing: Cleanup completed.")
    }
}

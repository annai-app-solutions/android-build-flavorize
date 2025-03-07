package com.annai.flavorize

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class FlavorizePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Register Pre-Build Task
        val preBuildTask = project.tasks.register("preBuildProcessing", PreBuildProcessingTask::class.java)

        // Register Post-Build Task
        val postBuildTask = project.tasks.register("postBuildProcessing", PostBuildProcessingTask::class.java)

        // Ensure `preBuildProcessing` runs before ANY `assemble<Variant>` task
        project.afterEvaluate {
            project.tasks.matching { it.name.startsWith("assemble") }.configureEach { task ->
                task.dependsOn(preBuildTask)  // Run pre-build processing before assembling
                task.finalizedBy(postBuildTask)  // Run post-build processing after assembling
            }
        }
    }
}

// ✅ Pre-Build Processing Task
abstract class PreBuildProcessingTask : DefaultTask() {
    init {
        group = "build"
        description = "Prepares necessary configurations before the build starts"
    }

    @TaskAction
    fun executePreBuild() {
        println("⚙️ Pre-Build Processing: Setting up flavors & configurations...")
    }
}

// ✅ Post-Build Processing Task
abstract class PostBuildProcessingTask : DefaultTask() {
    init {
        group = "build"
        description = "Performs cleanup or additional processing after the build completes"
    }

    @TaskAction
    fun executePostBuild() {
        println("✅ Post-Build Processing: Cleanup & finalizing tasks after build completion...")
    }
}

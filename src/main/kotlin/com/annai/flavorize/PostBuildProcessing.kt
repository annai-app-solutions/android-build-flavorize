package com.annai.flavorize

import com.annai.flavorize.spec.AnnaiSpecUtil
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import com.annai.flavorize.utils.*
import org.gradle.api.tasks.Internal

// ✅ Post-Build Task
abstract class PostBuildProcessingTask : DefaultTask() {

    @Internal
    lateinit var specUtil: AnnaiSpecUtil

    init {
        group = "build"
        description = "Post-build processing task"
    }

    @TaskAction
    fun executePostBuild() {
        println("✅ $pluginName Post-Build Processing: completed.")
    }
}

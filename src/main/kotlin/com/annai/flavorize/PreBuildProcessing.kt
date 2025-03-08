package com.annai.flavorize

import com.annai.flavorize.spec.AnnaiSpecUtil
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import com.annai.flavorize.utils.*

// ✅ Pre-Build Task
abstract class PreBuildProcessingTask : DefaultTask() {

    @Internal
    lateinit var specUtil: AnnaiSpecUtil

    init {
        group = "build"
        description = "Pre-build processing task"
    }

    @TaskAction
    fun executePreBuild() {

        println("⚙️ $pluginName Pre-Build Processing: Started...")

        specUtil.determineCurrentFlavor()

        specUtil.printFlavorInfo()
    }
}

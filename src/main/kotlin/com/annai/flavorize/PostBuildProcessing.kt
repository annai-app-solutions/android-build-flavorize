package com.annai.flavorize

import com.annai.flavorize.spec.AnnaiSpecUtil
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import com.annai.flavorize.utils.*
import org.gradle.api.tasks.Internal
import java.io.File

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

        // Retrieve file path from extra properties
        val destinationFilePath = project.extensions.extraProperties["firebaseDestinationFile"] as? String

        if (destinationFilePath != null) {
            val destinationFile = File(destinationFilePath)
            if (destinationFile.exists()) {
                destinationFile.delete()
                println("🗑️ Firebase Configuration File Deleted: ${destinationFile.absolutePath}")
            } else {
                println("⚠️ Firebase Configuration File not found for deletion.")
            }
        }

        println("✅ $pluginName Post-Build Processing: completed.")
    }
}

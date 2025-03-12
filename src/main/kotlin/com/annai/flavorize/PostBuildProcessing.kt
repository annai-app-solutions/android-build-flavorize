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

        if (project.extensions.extraProperties.has("firebaseDestinationFile")) {
            // Retrieve file path from extra properties
            val destinationFilePath = project.extensions.extraProperties["firebaseDestinationFile"] as? String?

            if (!destinationFilePath.isNullOrBlank()) {
                val destinationFile = File(destinationFilePath)
                if (destinationFile.exists()) {
                    destinationFile.delete()
                    println("🗑️ Firebase Configuration File Deleted: ${destinationFile.absolutePath}")
                } else {
                    println("⚠️ firebaseDestinationFile property is empty or null.")
                }
            }
        }

        println("✅ $pluginName Post-Build Processing: completed.")
    }
}

package com.annai.flavorize

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.regex.Pattern

class FlavorizePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val preBuildTask = project.tasks.register("preBuildProcessing", PreBuildProcessingTask::class.java)
        val postBuildTask = project.tasks.register("postBuildProcessing", PostBuildProcessingTask::class.java)

        // ✅ Hook into task creation
        project.tasks.configureEach { task ->
            if (task.name.matches(Regex("(assemble|bundle|generate)\\w*(Release|Debug|Profile)"))) {
                val variantName = extractFlavorAndBuildType(task.name)

                println("🔍 Extracted: Flavor = ${variantName.split("-")[0]}, BuildType = ${variantName.split("-")[1]}")
                println("🔹 Configuring Variant: $variantName for Task: ${task.name}")

                preBuildTask.configure { it.variantName = variantName }

                task.dependsOn(preBuildTask)
                task.finalizedBy(postBuildTask)
            }
        }
    }

    private fun extractFlavorAndBuildType(taskName: String): String {
        val pattern = Pattern.compile("(assemble|bundle|generate)([A-Z][a-zA-Z0-9]*)(Release|Debug|Profile)")
        val matcher = pattern.matcher(taskName)

        return if (matcher.find()) {
            val flavor = matcher.group(2)?.takeIf { it.isNotBlank() } ?: "NoFlavor"
            val buildType = matcher.group(3) ?: "Unknown"

            // 🔹 Default to "Banking" if NoFlavor is detected
            val finalFlavor = if (flavor == "NoFlavor") "Banking" else flavor

            "$finalFlavor-$buildType"
        } else {
            "Banking-Debug"  // 🔹 Default to Banking-Debug if extraction fails
        }
    }
}

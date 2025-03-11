package com.annai.flavorize

import com.annai.flavorize.spec.AnnaiSpecUtil
import com.annai.flavorize.utils.capitalizeFirstChar
import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class FlavorizePlugin : Plugin<Project> {
    override fun apply(project: Project) {

        val specUtil = AnnaiSpecUtil(project)

        // ✅ Register AnnaiSpecUtil as an extension for Gradle scripts
        project.extensions.add("annaiSpec", specUtil)

        // ✅ Also store AnnaiSpecUtil in rootProject.extra so it's accessible from scripts like `annai_post_build.gradle.kts`
        val extraProperties = project.rootProject.extensions.extraProperties
        extraProperties.set("annaiSpec", specUtil)

        // ✅ Debugging: Print available extensions to ensure AnnaiSpecUtil is registered
        //project.extensions.extensionsSchema.forEach {
            //println("📢 Available Extension: ${it.name} -> ${it.publicType}")
        //}

        val android = project.extensions.findByType(AppExtension::class.java)

        if (android == null) {
            println("⚠️ Android Gradle Plugin not found! Make sure this plugin is applied in an Android project.")
            return
        }

         android.applicationVariants.all { variant ->
            val flavor = variant.flavorName?.capitalizeFirstChar() ?: "NoFlavor"
            val buildType = variant.buildType.name.capitalizeFirstChar()
            val variantName = "$flavor-$buildType"

            val firebaseCopyTask = project.tasks.register("firebaseCopyProcessing${variant.name.capitalizeFirstChar()}", FirebaseCopyTask::class.java) {
                it.specUtil = specUtil
            }

            val preBuildTask = project.tasks.register("preBuildProcessing${variant.name.capitalizeFirstChar()}", PreBuildProcessingTask::class.java) {
                it.specUtil = specUtil
            }

            val postBuildTask = project.tasks.register("postBuildProcessing${variant.name.capitalizeFirstChar()}", PostBuildProcessingTask::class.java) {
                it.specUtil = specUtil
            }

            // ✅ Ensure GoogleServices tasks depend on FirebaseCopyTask ONLY ONCE
             project.tasks.matching { task ->
                 task.name.matches(Regex("(process)${variant.name.capitalizeFirstChar()}(GoogleServices)"))
             }.configureEach { task ->
                    if (!task.dependsOn.contains(firebaseCopyTask)) {
                        task.dependsOn(firebaseCopyTask)
                        //println("📢 FirebaseCopyTask added before: ${task.name}")
                    }
                }

            project.tasks.matching { task ->
                task.name.matches(Regex("(assemble|bundle|generate)${variant.name.capitalizeFirstChar()}"))
            }.configureEach { task ->
                task.dependsOn(preBuildTask)
                task.finalizedBy(postBuildTask)
                //println("📢 Pre & Post build task added before: ${task.name}")
            }
        }
    }

}

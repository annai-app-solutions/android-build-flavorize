package com.annai.flavorize

import com.android.build.api.variant.AndroidComponentsExtension
import com.annai.flavorize.spec.AnnaiSpecUtil
import com.android.build.gradle.AppExtension
import com.annai.flavorize.tasks.AnnaiBuildConfigurator
import com.annai.flavorize.tasks.FirebaseCopyTask
import com.annai.flavorize.tasks.PostBuildProcessingTask
import com.annai.flavorize.tasks.PreBuildProcessingTask
import com.annai.flavorize.utils.*
import org.gradle.api.Plugin
import org.gradle.api.Project

class FlavorizePlugin : Plugin<Project> {
    override fun apply(project: Project) {

        val specUtil = AnnaiSpecUtil(project)
        val enabled = specUtil.config?.enabled ?: true

        if(!enabled)  return
        
        debugEnable = specUtil.config?.debug?.printDebug ?: false

        // ✅ Register AnnaiSpecUtil as an extension for Gradle scripts
        project.extensions.add("annaiSpec", specUtil)

        // ✅ Also store AnnaiSpecUtil in rootProject.extra so it's accessible from scripts like `annai_post_build.gradle.kts`
        val extraProperties = project.rootProject.extensions.extraProperties
        extraProperties.set("annaiSpec", specUtil)

        // ✅ Get the Android Components API
        val androidComponents = project.extensions.findByType(AndroidComponentsExtension::class.java)
        val android = project.extensions.findByType(AppExtension::class.java)

        if (androidComponents == null || android == null) {
            throwError("Android Gradle Plugin not found! Make sure this plugin is applied in an Android project.")
        }

        if (specUtil.config != null) {
            // ✅ Configure flavors and build types at the **correct lifecycle stage**
            androidComponents.finalizeDsl { extension ->
                printDebug("Configuring AnnaiBuildConfigurator BEFORE AGP finalizes DSL")
                AnnaiBuildConfigurator(project).configure(specUtil.config!!)
            }
        } else {
            printWarning("Android Components Extension not found! Ensure AGP is applied before this plugin.")
        }

        // ✅ Debugging: Print available extensions to ensure AnnaiSpecUtil is registered
        if(debugEnable) {
            project.extensions.extensionsSchema.forEach {
                printDebug("Available Extension: ${it.name} -> ${it.publicType}")
            }
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
                        printDebug("FirebaseCopyTask added before: ${task.name}")
                    }
                }

            project.tasks.matching { task ->
                task.name.matches(Regex("(assemble|bundle|generate)${variant.name.capitalizeFirstChar()}"))
            }.configureEach { task ->
                task.dependsOn(preBuildTask)
                task.finalizedBy(postBuildTask)
                printDebug("Pre & Post build task added before: ${task.name}")
            }
        }
    }

}

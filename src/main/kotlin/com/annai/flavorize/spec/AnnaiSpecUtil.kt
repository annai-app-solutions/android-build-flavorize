package com.annai.flavorize.spec

import com.android.build.api.dsl.ApplicationExtension
import com.annai.flavorize.data.spec.AnnaiSpecData
import com.annai.flavorize.data.spec.app.flavor.AnnaiAndroidFlavor
import com.annai.flavorize.utils.AnnaiFileUtils
import com.annai.flavorize.utils.printDebug
import com.annai.flavorize.utils.printWarning
import com.annai.flavorize.utils.throwError
import org.gradle.api.Project
import org.gradle.api.GradleException
import java.io.File

class AnnaiSpecUtil(private val project: Project) {

    private val rootPath: String
    private val yamlFile: File
    private val annaiSpecFile: String = "annaispec.yaml"
    private val pubSpecFile: String = "pubspec.yaml"

    var currentFlavor: AnnaiAndroidFlavor? = null
    var buildType: String = "debug"
    var config: AnnaiSpecData? = null

    init {

        val flutterRoot = File(project.rootProject.projectDir.parentFile, pubSpecFile)
        val isFlutterProject = flutterRoot.exists()

        rootPath = if (isFlutterProject) {
            project.rootProject.projectDir.parentFile.canonicalPath // Move to Flutter root
        } else {
            project.rootProject.projectDir.canonicalPath // Native Android project
        }

        yamlFile = File(rootPath, annaiSpecFile)

        config = getDataFile(yamlFile)?.apply {
            annai_app.mergeDefaults()
            annai_app.validate()
        }
    }

    private fun getDataFile(annaiSpecFile: File): AnnaiSpecData? {
        if (!annaiSpecFile.exists()) {
            printWarning("Missing $annaiSpecFile file at ${annaiSpecFile.canonicalPath}")
            return null
        }
        return try {
            AnnaiFileUtils.loadYaml(annaiSpecFile.canonicalPath, AnnaiSpecData::class.java)
        } catch (exception: Exception) {
            println(exception.printStackTrace())
            throwError("Error reading $annaiSpecFile: ${exception.message}", GradleException::class, exception)
        }
    }

    fun determineCurrentFlavor() {
        val requestedTasks = project.gradle.startParameter.taskRequests.flatMap { it.args }

        val flavors = config?.annai_app?.android?.flavor ?: emptyMap()

        printDebug("Requested Tasks: $requestedTasks")
        printDebug("Available Flavors: ${flavors.keys}")

        // Find a matching flavor by checking if the task name contains the flavor key
        val detectedFlavor = flavors.keys.firstOrNull { flavorKey ->
            requestedTasks.any { task -> task.contains(flavorKey, ignoreCase = true) }
        }

        // Determine the build type (default to "debug" if not explicitly found)
        buildType = listOf("debug", "release", "profile")
            .firstOrNull { type -> requestedTasks.any { it.contains(type, ignoreCase = true) } }
            ?: "debug"

        printDebug("Detected Flavor: $detectedFlavor")
        printDebug("Detected buildType: $buildType")

        currentFlavor = detectedFlavor?.let { flavors[it] }

        if (currentFlavor == null) {
            throwError("No valid flavor detected! Available flavors: ${
                flavors.keys.joinToString(", ").ifEmpty { "None" }
            }")
        }
    }


    fun printFlavorInfo() {
        if(config?.debug?.printBuildAndFlavorInfo == false){
            return
        }

        if (currentFlavor == null) {
            printWarning("currentFlavor is not initialized! Calling determineCurrentFlavor()...")
            determineCurrentFlavor()
        }

        println("🛠 Build Information 🛠")
        if (currentFlavor != null) {
            val it = currentFlavor!!
            println("\t🔹 Build Type: $buildType")
            println("\t🔹 Flavor: ${it.flavorName}")
            println("\t🔹 App Name: ${it.finalName}")
            println("\t🔹 App ID: ${it.finalId}")
            println("\t🔹 Version Name: ${it.finalVersionName}")
            println("\t🔹 Version Code: ${it.version_code}")
        }  else {
            println("\t🔹 Build Type: $buildType")
            println("\t🔹 No flavor detected...")
        }
    }

    fun printSdkInfo() {

        if(config?.debug?.printSdkVersions == false){
            return
        }

        val androidExtension = project.extensions.findByType(ApplicationExtension::class.java)

        if (androidExtension != null) {
            val minSdk = androidExtension.defaultConfig.minSdk ?: "Unknown"
            val targetSdk = androidExtension.defaultConfig.targetSdk ?: "Unknown"
            val compileSdk = androidExtension.compileSdk ?: "Unknown"

            println("🛠 SDK Information 🛠")
            println("\t🔹 minSdk: $minSdk")
            println("\t🔹 targetSdk: $targetSdk")
            println("\t🔹 compileSdk: $compileSdk")
        } else {
            printWarning("Android extension not found! Make sure this task is applied in an Android project.")
        }
    }

    fun printReleaseBuildTypeInfo() {

        if(config?.debug?.printReleaseBuildTypeInfo == false){
            return
        }

        val androidExtension = project.extensions.findByType(ApplicationExtension::class.java)

        if (androidExtension != null) {
            val releaseBuild = androidExtension.buildTypes.getByName("release")
            val isShrinkResources = releaseBuild.isShrinkResources
            val isMinifyEnabled = releaseBuild.isMinifyEnabled
            val ndkVersion = androidExtension.ndkVersion
            val ndkDebugSymbolLevel = releaseBuild.ndk.debugSymbolLevel
            val ndkAbiFilters = releaseBuild.ndk.abiFilters
            val lintCheckReleaseBuilds = androidExtension.lint.checkReleaseBuilds

            println("🛠 Release BuildType Information 🛠")
            println("\t🔹 isShrinkResources: $isShrinkResources")
            println("\t🔹 isMinifyEnabled: $isMinifyEnabled")
            println("\t🔹 ndkVersion: $ndkVersion")
            println("\t🔹 ndkDebugSymbolLevel: $ndkDebugSymbolLevel")
            println("\t🔹 ndkAbiFilters: $ndkAbiFilters")
            println("\t🔹 lintCheckReleaseBuilds: $lintCheckReleaseBuilds")
        } else {
            printWarning("Android extension not found! Make sure this task is applied in an Android project.")
        }
    }

}


package com.annai.flavorize

import com.annai.flavorize.spec.AnnaiFirebase
import com.annai.flavorize.spec.AnnaiSpecUtil
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File

// ✅ Firebase File Copy Task
abstract class FirebaseCopyTask : DefaultTask() {

    @Internal
    lateinit var specUtil: AnnaiSpecUtil

    init {
        group = "build"
        description = "Copies the correct google-services.json file before the build"
    }

    @TaskAction
    fun copyFirebaseConfigFile() {
        println("⚙️ Starting Firebase Configuration Copy Task...")

        specUtil.determineCurrentFlavor()
        //specUtil.printFlavorInfo()

        val buildType = specUtil.buildType.lowercase()
        val flavor = specUtil.currentFlavor?.flavorName?.lowercase()
            ?: throw GradleException("❌ No valid flavor detected in Firebase Copy Task!")

        val firebaseConfig : AnnaiFirebase? = specUtil.currentFlavor?.firebase
        if(firebaseConfig == null) {
            println("⚠️ No Firebase configuration found for flavor: $flavor and buildType: $buildType")
            return
        }

        val relativeSourcePath = firebaseConfig.getFile(buildType)
            ?: throw GradleException("❌ No valid Firebase configuration file found for flavor: $flavor and buildType: $buildType")

        val projectRoot = project.projectDir.absolutePath
        val sourceFile = File("$projectRoot/../../$relativeSourcePath").canonicalFile

        val destinationFile = File("$projectRoot/${sourceFile.name}")

        //println("📢 Firebase Configuration - Source: ${sourceFile.absolutePath}")

        if (!sourceFile.exists()) {
            throw GradleException("❌ Firebase configuration file missing: ${sourceFile.absolutePath}")
        }

        // ✅ Perform file copy
        sourceFile.copyTo(destinationFile, overwrite = true)

        if (!destinationFile.exists()) {
            throw GradleException("❌ Firebase configuration file copy failed: ${destinationFile.absolutePath}")
        }

        // ✅ Store destinationFile path in `project.extra`
        project.extensions.extraProperties["firebaseDestinationFile"] = destinationFile.absolutePath

        println("✅ Firebase Configuration Copied Successfully!")
        println("\t🔹 From: ${sourceFile.absolutePath}")
        println("\t🔹 To: ${destinationFile.absolutePath}")
    }
}

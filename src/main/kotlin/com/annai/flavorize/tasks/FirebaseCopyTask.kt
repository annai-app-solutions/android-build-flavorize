package com.annai.flavorize.tasks

import com.annai.flavorize.data.spec.app.firebase.AnnaiFirebaseData
import com.annai.flavorize.spec.AnnaiSpecUtil
import com.annai.flavorize.utils.printDebug
import com.annai.flavorize.utils.printWarning
import com.annai.flavorize.utils.throwError
import org.gradle.api.DefaultTask
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
            ?: throwError("No valid flavor detected in Firebase Copy Task!")


        val firebaseConfig : AnnaiFirebaseData? = specUtil.currentFlavor?.firebase
        if(firebaseConfig == null) {
            printWarning("No Firebase configuration found for flavor: $flavor and buildType: $buildType")
            return
        }

        val relativeSourcePath = firebaseConfig.getConfigFile(buildType)
            ?: throwError("No valid Firebase configuration file found for flavor: $flavor and buildType: $buildType")

        val projectRoot = project.projectDir.absolutePath
        val sourceFile = File("$projectRoot/../../$relativeSourcePath").canonicalFile

        val destinationFile = File("$projectRoot/${sourceFile.name}")

        printDebug("Firebase Configuration - Source: ${sourceFile.absolutePath}")

        if (!sourceFile.exists()) {
            throwError("Firebase configuration file missing: ${sourceFile.absolutePath}")
        }

        // ✅ Perform file copy
        sourceFile.copyTo(destinationFile, overwrite = true)

        if (!destinationFile.exists()) {
            throwError("Firebase configuration file copy failed: ${destinationFile.absolutePath}")
        }

        // ✅ Store destinationFile path in `project.extra`
        project.extensions.extraProperties["firebaseDestinationFile"] = destinationFile.absolutePath

        println("✅ Firebase Configuration Copied Successfully!")
        println("\t🔹 From: ${sourceFile.absolutePath}")
        println("\t🔹 To: ${destinationFile.absolutePath}")
    }
}

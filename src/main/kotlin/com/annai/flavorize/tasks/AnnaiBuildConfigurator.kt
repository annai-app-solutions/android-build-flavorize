package com.annai.flavorize.tasks

import com.android.build.api.dsl.ApplicationExtension
import com.annai.flavorize.data.spec.AnnaiSpecData
import com.annai.flavorize.data.spec.app.AnnaiAndroid
import com.annai.flavorize.utils.printDebug
import com.annai.flavorize.utils.printWarning
import org.gradle.api.Project
import java.io.File
import java.util.Properties

class AnnaiBuildConfigurator(private val project: Project) {

    fun configure(specData: AnnaiSpecData) {
        val androidExtension = project.extensions.getByType(ApplicationExtension::class.java)

        val appData = specData.annai_app.android
        if (appData?.isValid == true && appData.flavor?.isNotEmpty() == true) {
            configureSdk(appData, androidExtension)
            configureFlavors(appData, androidExtension)
            configureSigning(appData, androidExtension)
            configureBuildTypes(appData, androidExtension)
        } else {
            printWarning("Invalid android flavor data in the spec file")
        }
    }

    private fun configureSdk(appData: AnnaiAndroid, androidExtension: ApplicationExtension) {

        if(appData.sdk != null) {
            val sdk = appData.sdk!!
            androidExtension.apply {

                if (sdk.compileSdk != null) {
                    compileSdk = sdk.compileSdk
                }
                if (sdk.targetSdk != null) {
                    defaultConfig.targetSdk = sdk.targetSdk
                }
                if (sdk.minSdk != null) {
                    defaultConfig.minSdk = sdk.minSdk
                }

            }
        }
    }

    private fun configureFlavors(appData: AnnaiAndroid, androidExtension: ApplicationExtension) {
        androidExtension.apply {
            flavorDimensions.add("variant")

            productFlavors {
                val allFlavors = appData.flavor!!

                allFlavors.forEach { (flavorName, flavor) ->
                    create(flavorName) {
                        it.dimension = "variant"
                        it.applicationId = flavor.finalId
                        it.resValue("string", "app_name", flavor.finalName)
                        it.versionName = flavor.finalVersionName
                        it.versionCode = flavor.version_code
                        if(flavor.gms_ads_id != null) {
                            it.resValue("string", "gms_ads_id", flavor.gms_ads_id!!)
                        }
                    }
                }
            }
        }
    }

    private fun configureSigning(appData: AnnaiAndroid, androidExtension: ApplicationExtension) {


        val keyFilePath = appData.default.signature?.key_file.orEmpty()

        val keyFile = findValidFile(keyFilePath)

        if (keyFile == null) {
            printWarning("Invalid/No Signature file found: $keyFilePath")
            return
        }

        val keyProperties = Properties().apply {
            keyFile.reader(Charsets.UTF_8).use { load(it) }
        }

        val keystoreFilePath = keyProperties["keyStoreFile"] as String? ?: ""
        val keystoreFile = findValidFile(keystoreFilePath)

        if (keystoreFile == null) {
            printWarning("Invalid/No Signature keystore file found: $keystoreFilePath")
            return
        }
        androidExtension.apply {

            signingConfigs {
                create("release") {
                    it.keyAlias = keyProperties["keyAlias"] as String?
                    it.keyPassword = keyProperties["keyPassword"] as String?
                    it.storeFile = keystoreFile
                    it.storePassword = keyProperties["storePassword"] as String?
                }
            }

            buildTypes {
                getByName("release") {
                    it.signingConfig = signingConfigs.getByName("release")
                }
            }

        }
    }

    private fun configureBuildTypes(appData: AnnaiAndroid, androidExtension: ApplicationExtension) {
        androidExtension.apply {
            ndkVersion = appData.releaseBuildTypes?.ndkVersion ?: ndkVersion

            buildTypes {
                getByName("release") {
                    it.isShrinkResources = appData.releaseBuildTypes?.shrinkResources ?: it.isShrinkResources
                    it.isMinifyEnabled = appData.releaseBuildTypes?.minifyEnabled ?: it.isMinifyEnabled

                    it.ndk.debugSymbolLevel = appData.releaseBuildTypes?.ndkDebugSymbolLevel ?: it.ndk.debugSymbolLevel
                    if (!appData.releaseBuildTypes?.ndkAbiFilters.isNullOrEmpty()) {
                        it.ndk.abiFilters.clear()
                        it.ndk.abiFilters.addAll(appData.releaseBuildTypes!!.ndkAbiFilters!!)
                    }
                }
            }

            lint {
                checkReleaseBuilds = appData.releaseBuildTypes?.lintCheckReleaseBuilds ?: checkReleaseBuilds
            }

         }
    }

    private fun findValidFile(filePath: String): File? {

        if(filePath.isBlank()) {
            return null
        }

        val locations = listOf(
            File(filePath),
            File(project.rootProject.projectDir, filePath),
            File(project.rootProject.projectDir.parent, filePath) // Parent of rootProject
        )

        for (file in locations) {
            if (file.exists()) return file
        }

        printDebug("File not found: $filePath")
        return null
    }

}

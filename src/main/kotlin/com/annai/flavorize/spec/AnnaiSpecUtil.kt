package com.annai.flavorize.spec

import org.gradle.api.Project
import org.gradle.api.GradleException
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.util.*

class AnnaiSpecUtil(private val project: Project) {

    private val appPath = project.projectDir.canonicalPath
    private val androidRootPath = project.rootProject.projectDir.canonicalPath
    private val rootPath = File(project.rootProject.projectDir, "../").canonicalPath
    private val annaiKeyDataPath = File(rootPath, "../annai_app_data/keys/android").canonicalPath

    private val yamlFile = File(rootPath, "annaispec.yaml")
    private val passFile = File(annaiKeyDataPath, "annai_key.properties")

    val keyStoreFile = File(annaiKeyDataPath, "annai_key.jks")
    val keystoreProperties = Properties()
    var allFlavors: List<AnnaiFlavor> = emptyList()
    var currentFlavor: AnnaiFlavor? = null
    var buildType = ""

    init {
        parseYamlFile(yamlFile)
        parseSignatureFiles(passFile)
    }

    private fun parseYamlFile(specFile: File) {
        if (!specFile.exists()) {
            throw GradleException("❌ annaispec.yaml file not found: ${specFile.absolutePath}")
        }

        val yaml = Yaml()
        val data: Map<String, Any> = yaml.load(specFile.inputStream())

        val appData = data["annai_app"] as? Map<String, Any> ?: emptyMap()
        val appAndroidData = appData["android"] as? Map<String, Any> ?: emptyMap()

        val defaults = appAndroidData["default"] as? Map<String, Any> ?: emptyMap()
        val flavorsMap = appAndroidData["flavor"] as? Map<String, Map<String, Any>> ?: emptyMap()

        if (flavorsMap.isEmpty()) {
            throw GradleException("❌ No flavors found in annaispec.yaml!")
        }

        // 🔹 Store all flavors
        allFlavors = flavorsMap.map { (flavorKey, flavorData) ->
            AnnaiFlavor(
                flavorName = flavorKey,
                name = safeString(flavorData["name"] ?: defaults["name"]),
                id = safeString(flavorData["id"] ?: defaults["id"]),
                idSuffix = safeString(flavorData["id_suffix"]),
                versionName = safeString(flavorData["version_name"] ?: defaults["version_name"]),
                versionNameSuffix = safeString(flavorData["version_name_suffix"]),
                versionCode = safeInt(flavorData["version_code"] ?: defaults["version_code"]),
                firebase = processFirebaseData(flavorData["firebase"], defaults["firebase"]),
            )
        }
    }

    private fun processFirebaseData(flavorFirebase: Any?, defaultFirebase: Any?): AnnaiFirebase? {

        var releaseFile: String? = null;
        var debugFile: String? = null;
        var profileFile: String? = null;

        var firebase = defaultFirebase as? Map<String, Any> ?: emptyMap()

        if(firebase.isNotEmpty()) {
            var firebaseBuildType = firebase["release"] as? Map<String, Any> ?: emptyMap()
            if (firebaseBuildType.isNotEmpty()) {
                releaseFile = firebaseBuildType["file"] as? String?
            }
            firebaseBuildType = firebase["debug"] as? Map<String, Any> ?: emptyMap()
            if (firebaseBuildType.isNotEmpty()) {
                debugFile = firebaseBuildType["file"] as? String?
            }
            firebaseBuildType = firebase["profile"] as? Map<String, Any> ?: emptyMap()
            if (firebaseBuildType.isNotEmpty()) {
                profileFile = firebaseBuildType["file"] as? String?
            }
        }

        firebase = flavorFirebase as? Map<String, Any> ?: emptyMap()

        if(firebase.isNotEmpty()) {
            var firebaseBuildType = firebase["release"] as? Map<String, Any> ?: emptyMap()
            if (firebaseBuildType.isNotEmpty()) {
                releaseFile = firebaseBuildType["file"] as? String?
            }
            firebaseBuildType = firebase["debug"] as? Map<String, Any> ?: emptyMap()
            if (firebaseBuildType.isNotEmpty()) {
                debugFile = firebaseBuildType["file"] as? String?
            }
            firebaseBuildType = firebase["profile"] as? Map<String, Any> ?: emptyMap()
            if (firebaseBuildType.isNotEmpty()) {
                profileFile = firebaseBuildType["file"] as? String?
            }
        }

        if(releaseFile == null && debugFile == null && profileFile == null) {
            return null
        }

        return AnnaiFirebase(
            releaseFile = releaseFile,
            debugFile = debugFile,
            profileFile = profileFile,
        )
    }

    fun determineCurrentFlavor() {
        val requestedTasks = project.gradle.startParameter.taskRequests
            .flatMap { it.args }

        // Extract the flavor from command like `assembleBankingDebug`
        val detectedFlavor = allFlavors.map { it.flavorName }
            .firstOrNull { flavor -> requestedTasks.any { it.contains(flavor, ignoreCase = true) } }

        val buildTypeFromTask = listOf("debug", "release", "profile")
            .firstOrNull { type -> requestedTasks.any { it.contains(type, ignoreCase = true) } } ?: "debug"

        //println("📢 Detected Tasks: $requestedTasks")
        //println("📢 Extracted Flavor: $detectedFlavor")
        //println("📢 Extracted Build Type: $buildTypeFromTask")

        currentFlavor = detectedFlavor?.let { getFlavor(it) }
        buildType = buildTypeFromTask

        if (currentFlavor == null) {
            throw GradleException("❌ Could not detect a valid flavor from the command!")
        }
    }

    private fun getFlavor(flavorName: String?): AnnaiFlavor {

        return allFlavors.firstOrNull { it.flavorName.equals(flavorName, ignoreCase = true) }
            ?: throw GradleException("❌ Flavor '$flavorName' not found in annaispec.yaml.")
    }

    private fun parseSignatureFiles(passFile: File) {
        if (passFile.exists()) {
            passFile.reader(Charsets.UTF_8).use { reader ->
                keystoreProperties.load(reader)
            }
        } else {
            println("⚠️ Signature properties file not found: ${passFile.absolutePath}")
        }
    }

    private fun safeInt(value: Any?): Int {
        return when (value) {
            is Int -> value
            is String -> value.toIntOrNull() ?: 1
            else -> 1
        }
    }

    private fun safeString(value: Any?): String {
        return value?.toString() ?: ""
    }

    fun printFlavorInfo() {
        if (currentFlavor == null) {
            println("⚠️ currentFlavor is not initialized! Calling determineCurrentFlavor()...")
            determineCurrentFlavor()
        }

        if (currentFlavor != null) {
            println("   🔹 Flavor: ${currentFlavor!!.flavorName}")
            println("   🔹 Build Type: $buildType")
            println("   🔹 App Name: ${currentFlavor!!.name}")
            println("   🔹 App ID: ${currentFlavor!!.finalId}")
            println("   🔹 Version Name: ${currentFlavor!!.finalVersionName}")
            println("   🔹 Version Code: ${currentFlavor!!.versionCode}")
        } else {
            println("⚠️ No flavor detected...")
            println("   🔹 Build Type: $buildType")
        }
    }

}

data class AnnaiFlavor(
    var flavorName: String,
    var id: String,
    var name: String,
    var idSuffix: String = "",
    var versionName: String,
    var versionNameSuffix: String = "",
    var versionCode: Int,
    var firebase: AnnaiFirebase?,
) {
    val finalId: String
        get() = id + idSuffix

    val finalVersionName: String
        get() = versionName + versionNameSuffix
}

data class AnnaiFirebase(
    var releaseFile: String?,
    var debugFile: String?,
    var profileFile: String?,
) {
    fun getFile(buildType: String): String? {
        return when (buildType.lowercase()) {
            "release" -> releaseFile
            "debug" -> debugFile.takeIf { !it.isNullOrBlank() } ?: releaseFile
            "profile" -> profileFile.takeIf { !it.isNullOrBlank() }
                ?: debugFile.takeIf { !it.isNullOrBlank() }
                ?: releaseFile
            else -> throw IllegalArgumentException("❌ Unknown build type: $buildType")
        }
    }
}

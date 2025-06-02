package com.annai.flavorize.data.spec.app.firebase

import com.annai.flavorize.utils.throwError


/**
 * Base interface for common Firebase configuration properties.
 */
interface FirebaseConfigBase {
    var project_id: String?
    var firebase_app_id: String?
}

/**
 * Firebase configuration specific to Android.
 * Contains project ID, Google App ID, and the path to the configuration file.
 */
data class AndroidFirebaseConfig(
    override var project_id: String? = null,
    override var firebase_app_id: String? = null,
    var path: String? = null,
) : FirebaseConfigBase

/**
 * Firebase configuration specific to iOS.
 * Contains project ID, Google App ID, path to the config file, and the build target.
 */
data class IosFirebaseConfig(
    override var project_id: String? = null,
    override var firebase_app_id: String? = null,
    var path: String? = null,
    var build_target: String? = null,
) : FirebaseConfigBase

/**
 * Firebase configuration specific to Windows.
 * Contains project ID and Google App ID.
 */
data class WindowsFirebaseConfig(
    override var project_id: String? = null,
    override var firebase_app_id: String? = null,
) : FirebaseConfigBase

/**
 * Firebase configuration specific to Web.
 * Contains project ID and Google App ID.
 */
data class WebFirebaseConfig(
    override var project_id: String? = null,
    override var firebase_app_id: String? = null,
) : FirebaseConfigBase


// --- Platform-Specific Firebase Data Containers ---

/**
 * Base interface for platform-specific Firebase data classes.
 * Defines methods to retrieve configuration details with fallback logic.
 * T is the specific FirebaseConfig type for the platform (e.g., AndroidFirebaseConfig).
 */
interface PlatformFirebaseData<T : FirebaseConfigBase> {
    val release: T?
    val debug: T?
    val profile: T?

    fun getConfigPath(buildType: String): String? {
        val config = when (buildType.lowercase()) {
            "release" -> release
            "debug" -> debug ?: release
            "profile" -> profile ?: debug ?: release
            else -> throwError("Unknown build type: $buildType", IllegalArgumentException::class)
        }
        // Only return path if the config type supports it
        return if (config is AndroidFirebaseConfig) config.path
        else if (config is IosFirebaseConfig) config.path
        else null // Windows and Web do not have 'path'
    }

    fun getConfigFile(buildType: String): String? {
        val config = when (buildType.lowercase()) {
            "release" -> release
            "debug" -> debug ?: release
            "profile" -> profile ?: debug ?: release
            else -> throwError("Unknown build type: $buildType", IllegalArgumentException::class)
        }
        // Only return path if the config type supports it
        return if (config is AndroidFirebaseConfig) config.path + "/google-services.json"
        else if (config is IosFirebaseConfig) config.path + "/GoogleService-Info.plist"
        else null // Windows and Web do not have 'path'
    }

    fun getConfigGoogleAppId(buildType: String): String? {
        val config = when (buildType.lowercase()) {
            "release" -> release
            "debug" -> debug ?: release
            "profile" -> profile ?: debug ?: release
            else -> throwError("Unknown build type: $buildType", IllegalArgumentException::class)
        }
        return config?.firebase_app_id.takeIf { !it.isNullOrBlank() }
    }

    fun getConfigProjectId(buildType: String): String? {
        val config = when (buildType.lowercase()) {
            "release" -> release
            "debug" -> debug ?: release
            "profile" -> profile ?: debug ?: release
            else -> throwError("Unknown build type: $buildType", IllegalArgumentException::class)
        }
        return config?.project_id.takeIf { !it.isNullOrBlank() }
    }

    // New: Method to get the entire FirebaseConfig object for a given build type
    fun getFirebaseConfig(buildType: String): T? {
        return when (buildType.lowercase()) {
            "release" -> release
            "debug" -> debug ?: release
            "profile" -> profile ?: debug ?: release
            else -> throwError("Unknown build type: $buildType", IllegalArgumentException::class)
        }
    }
}

/**
 * Firebase configuration data specific to Android platform.
 */
data class AnnaiAndroidFirebaseData(
    override var release: AndroidFirebaseConfig? = null,
    override var debug: AndroidFirebaseConfig? = null,
    override var profile: AndroidFirebaseConfig? = null,
) : PlatformFirebaseData<AndroidFirebaseConfig>

/**
 * Firebase configuration data specific to iOS platform.
 */
data class AnnaiIosFirebaseData(
    override var release: IosFirebaseConfig? = null,
    override var debug: IosFirebaseConfig? = null,
    override var profile: IosFirebaseConfig? = null,
) : PlatformFirebaseData<IosFirebaseConfig> {
    fun getConfigBuildTarget(buildType: String): String? {
        val config = getFirebaseConfig(buildType)
        return (config as? IosFirebaseConfig)?.build_target.takeIf { !it.isNullOrBlank() }
    }
}

/**
 * Firebase configuration data specific to Windows platform.
 */
data class AnnaiWindowsFirebaseData(
    override var release: WindowsFirebaseConfig? = null,
    override var debug: WindowsFirebaseConfig? = null,
    override var profile: WindowsFirebaseConfig? = null,
) : PlatformFirebaseData<WindowsFirebaseConfig>

/**
 * Firebase configuration data specific to Web platform.
 */
data class AnnaiWebFirebaseData(
    override var release: WebFirebaseConfig? = null,
    override var debug: WebFirebaseConfig? = null,
    override var profile: WebFirebaseConfig? = null,
) : PlatformFirebaseData<WebFirebaseConfig>
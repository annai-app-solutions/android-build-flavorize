package com.annai.flavorize.data.spec.app.firebase

import com.annai.flavorize.utils.throwError

data class AnnaiFirebaseData (
    var release: FirebaseConfig? = null,
    var debug: FirebaseConfig? = null,
    var profile: FirebaseConfig? = null,
) {

    fun getConfigFile(buildType: String): String? {
        return when (buildType.lowercase()) {
            "release" -> release?.file
            "debug" -> debug?.file.takeIf { !it.isNullOrBlank() } ?: release?.file
            "profile" -> profile?.file.takeIf { !it.isNullOrBlank() }
                ?: debug?.file.takeIf { !it.isNullOrBlank() }
                ?: release?.file
            else -> throwError("Unknown build type: $buildType", IllegalArgumentException::class)
        }
    }

    fun getConfigGoogleAppId(buildType: String): String? {
        return when (buildType.lowercase()) {
            "release" -> release?.google_app_id
            "debug" -> debug?.google_app_id.takeIf { !it.isNullOrBlank() } ?: release?.google_app_id
            "profile" -> profile?.google_app_id.takeIf { !it.isNullOrBlank() }
                ?: debug?.google_app_id.takeIf { !it.isNullOrBlank() }
                ?: release?.google_app_id
            else -> throwError("Unknown build type: $buildType", IllegalArgumentException::class)
        }
    }

    fun getConfigProjectId(buildType: String): String? {
        return when (buildType.lowercase()) {
            "release" -> release?.project_id
            "debug" -> debug?.project_id.takeIf { !it.isNullOrBlank() } ?: release?.project_id
            "profile" -> profile?.project_id.takeIf { !it.isNullOrBlank() }
                ?: debug?.project_id.takeIf { !it.isNullOrBlank() }
                ?: release?.project_id
            else -> throwError("Unknown build type: $buildType", IllegalArgumentException::class)
        }
    }
}

data class FirebaseConfig (
    var project_id: String? = null,
    var google_app_id: String? = null,
    var file: String? = null,
    //var firebase_options: FirebaseOptions? = null,
)


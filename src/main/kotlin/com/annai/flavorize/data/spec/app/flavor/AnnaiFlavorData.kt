package com.annai.flavorize.data.spec.app.flavor

import com.annai.flavorize.data.spec.app.firebase.AnnaiAndroidFirebaseData
import com.annai.flavorize.data.spec.app.firebase.AnnaiIosFirebaseData
import com.annai.flavorize.data.spec.app.firebase.AnnaiWebFirebaseData
import com.annai.flavorize.data.spec.app.firebase.AnnaiWindowsFirebaseData
import com.annai.flavorize.data.spec.app.inapp.AnnaiAuthData
import com.annai.flavorize.data.spec.app.inapp.AnnaiInAppSubscription
import com.annai.flavorize.spec.AnnaiDataUtils
import com.annai.flavorize.utils.throwError

data class AnnaiAndroidFlavor (
    var id: String? = null,
    var id_suffix: String? = null,
    var name: String? = null,
    var name_suffix: String? = null,
    var version_name: String? = null,
    var version_name_suffix: String? = null,
    var version_code: Int? = null,
    var priority: Int? = null,
    var main_file: String? = null,
    var gms_ads_id: String? = null,
    var firebase: AnnaiAndroidFirebaseData? = null,
    var in_app_subscription: List<AnnaiInAppSubscription>? = null,
    var auth: AnnaiAuthData? = null,

    ) {

    var flavorName: String? = null

    val isValid: Boolean
        get()  {
            return AnnaiDataUtils.isValidId(finalId)
                    && AnnaiDataUtils.isValidVersionCode(version_code)
        }

    fun validate(): Boolean {
        if (!AnnaiDataUtils.isValidId(finalId)) {
            throw IllegalArgumentException("❌ Android flavor app ID is missing!")
        }
        if (!AnnaiDataUtils.isValidVersionCode(version_code)) {
            throw IllegalArgumentException("❌ Android flavor version_code is missing!")
        }

        return true
    }

    val finalId: String
        get() = id.orEmpty() + id_suffix.orEmpty()

    val finalName: String
        get() = name.orEmpty() + name_suffix.orEmpty()

    val finalVersionName: String
        get() = version_name.orEmpty() + version_name_suffix.orEmpty()
}

data class AnnaiIosFlavor (
    var id: String? = null,
    var id_suffix: String? = null,
    var name: String? = null,
    var name_suffix: String? = null,
    var version_name: String? = null,
    var version_name_suffix: String? = null,
    var version_code: Int? = null,
    var main_file: String? = null,
    var apple_id: String? = null,
    var gms_ads_id: String? = null,
    var firebase: AnnaiIosFirebaseData? = null,
    var in_app_subscription: List<AnnaiInAppSubscription>? = null,
    var auth: AnnaiAuthData? = null,

    ) {
    var flavorName: String? = null

    val isValid: Boolean
        get()  {
            return AnnaiDataUtils.isValidId(finalId)
                    && AnnaiDataUtils.isValidVersionCode(version_code)
        }

    fun validate(): Boolean {
        if (!AnnaiDataUtils.isValidId(finalId)) {
            throw IllegalArgumentException("❌ iOS flavor app ID is missing!")
        }
        if (!AnnaiDataUtils.isValidVersionCode(version_code)) {
            throw IllegalArgumentException("❌ iOS flavor version_code is missing!")
        }

        return true
    }

    val finalId: String
        get() = id.orEmpty() + id_suffix.orEmpty()

    val finalName: String
        get() = name.orEmpty() + name_suffix.orEmpty()

    val finalVersionName: String
        get() = version_name.orEmpty() + version_name_suffix.orEmpty()
}

data class AnnaiWebFlavor (
    var id: String? = null,
    var id_suffix: String? = null,
    var name: String? = null,
    var name_suffix: String? = null,
    var version_name: String? = null,
    var version_name_suffix: String? = null,
    var version_code: Int? = null,
    var main_file: String? = null,
    var firebase: AnnaiWebFirebaseData? = null,
    var in_app_subscription: List<AnnaiInAppSubscription>? = null,
    var auth: AnnaiAuthData? = null,

    ) {
    var flavorName: String? = null

    val isValid: Boolean
        get()  {
            return AnnaiDataUtils.isValidId(finalId)
                    && AnnaiDataUtils.isValidVersionCode(version_code)
        }

    fun validate(): Boolean {
        if (!AnnaiDataUtils.isValidId(finalId)) {
            throwError("Web flavor app ID is missing!", IllegalArgumentException::class)
        }
        if (!AnnaiDataUtils.isValidVersionCode(version_code)) {
            throwError("Web flavor version_code is missing!", IllegalArgumentException::class)
        }

        return true
    }


    val finalId: String
        get() = id.orEmpty() + id_suffix.orEmpty()

    val finalName: String
        get() = name.orEmpty() + name_suffix.orEmpty()

    val finalVersionName: String
        get() = version_name.orEmpty() + version_name_suffix.orEmpty()
}

data class AnnaiWindowsFlavor (
    var id: String? = null,
    var id_suffix: String? = null,
    var name: String? = null,
    var name_suffix: String? = null,
    var version_name: String? = null,
    var version_name_suffix: String? = null,
    var version_code: Int? = null,
    var main_file: String? = null,
    var firebase: AnnaiWindowsFirebaseData? = null,
    var in_app_subscription: List<AnnaiInAppSubscription>? = null,
    var auth: AnnaiAuthData? = null,

    ) {
    var flavorName: String? = null

    val isValid: Boolean
        get()  {
            return AnnaiDataUtils.isValidId(finalId)
                    && AnnaiDataUtils.isValidVersionCode(version_code)
        }

    fun validate(): Boolean {
        if (!AnnaiDataUtils.isValidId(finalId)) {
            throwError("Windows flavor app ID is missing!", IllegalArgumentException::class)
        }
        if (!AnnaiDataUtils.isValidVersionCode(version_code)) {
            throwError("Windows flavor version_code is missing!", IllegalArgumentException::class)
        }

        return true
    }


    val finalId: String
        get() = id.orEmpty() + id_suffix.orEmpty()

    val finalName: String
        get() = name.orEmpty() + name_suffix.orEmpty()

    val finalVersionName: String
        get() = version_name.orEmpty() + version_name_suffix.orEmpty()
}
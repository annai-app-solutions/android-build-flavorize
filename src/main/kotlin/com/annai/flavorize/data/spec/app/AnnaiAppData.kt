package com.annai.flavorize.data.spec.app

import com.annai.flavorize.data.spec.app.fastlane.FastlaneAndroidData
import com.annai.flavorize.data.spec.app.fastlane.FastlaneIosData
import com.annai.flavorize.data.spec.app.flavor.AnnaiAndroidFlavor
import com.annai.flavorize.data.spec.app.flavor.AnnaiIosFlavor
import com.annai.flavorize.data.spec.app.flavor.AnnaiWebFlavor
import com.annai.flavorize.data.spec.app.inapp.AnnaiInAppSubscription
import com.annai.flavorize.spec.AnnaiDataUtils
import com.annai.flavorize.data.merger.mergeWithDefault
import com.annai.flavorize.data.spec.app.bundle.AnnaiAndroidKeysData
import com.annai.flavorize.data.spec.app.firebase.AnnaiAndroidFirebaseData
import com.annai.flavorize.data.spec.app.firebase.AnnaiIosFirebaseData
import com.annai.flavorize.data.spec.app.firebase.AnnaiWebFirebaseData
import com.annai.flavorize.data.spec.app.firebase.AnnaiWindowsFirebaseData
import com.annai.flavorize.data.spec.app.flavor.AnnaiWindowsFlavor
import com.annai.flavorize.utils.throwError

data class AnnaiAppData (
    var android: AnnaiAndroid? = null,
    var ios: AnnaiIos? = null,
    var windows: AnnaiWindows? = null,
    var web: AnnaiWeb? = null,
)
{
    val isValid: Boolean
        get() = android?.isValid == true || ios?.isValid == true || windows?.isValid == true || web?.isValid == true

    fun validate(): Boolean {
        if (android?.validate() == false) return false
        if (ios?.validate() == false) return false
        if (windows?.validate() == false) return false
        if (web?.validate() == false) return false
        return true
    }

    fun mergeDefaults() {
        android?.mergeDefaults()
        ios?.mergeDefaults()
        windows?.mergeDefaults()
        web?.mergeDefaults()
    }
}

data class AnnaiAndroid (
    var sdk: AnnaiAndroidSdk? = null,
    var releaseBuildTypes: AnnaiAndroidBuildType? = null,
    var default: AnnaiAndroidDefaultApp = AnnaiAndroidDefaultApp(),
    var flavor: Map<String, AnnaiAndroidFlavor>? = null,
) {

    val isValid: Boolean
        get()  {
            return if(flavor?.values?.isEmpty() == true) {
                AnnaiDataUtils.isValidId(default.id)
                        && AnnaiDataUtils.isValidVersionCode(default.version_code)
            } else {
                flavor?.values?.all { it.isValid } ?: false
            }
        }

    fun validate(): Boolean {
        if(flavor?.values?.isEmpty() == true) {
            if (!AnnaiDataUtils.isValidId(default.id)) {
                throwError("Android default app ID is missing!", IllegalArgumentException::class)
            }
            if (!AnnaiDataUtils.isValidVersionCode(default.version_code)) {
                throwError("Android default version_code is missing!", IllegalArgumentException::class)
            }
        }

        flavor?.values?.forEach { it.validate() }
        return true
    }

    fun mergeDefaults() {
        flavor?.forEach { (name, flavor) ->
            flavor.mergeWithDefault(default, name)
        }
    }
}

data class AnnaiIos (
    var default: AnnaiIosDefaultApp = AnnaiIosDefaultApp(),
    var flavor: Map<String, AnnaiIosFlavor>? = null,
)
{
    val isValid: Boolean
        get()  {
            return if(flavor?.values?.isEmpty() == true) {
                AnnaiDataUtils.isValidId(default.id)
                        && AnnaiDataUtils.isValidVersionCode(default.version_code)
            } else {
                flavor?.values?.all { it.isValid } ?: false
            }
        }

    fun validate(): Boolean {
        if(flavor?.values?.isEmpty() == true) {
            if (!AnnaiDataUtils.isValidId(default.id)) {
                throwError("iOS default app ID is missing!", IllegalArgumentException::class)
            }
            if (!AnnaiDataUtils.isValidVersionCode(default.version_code)) {
                throwError("iOS default version_code is missing!", IllegalArgumentException::class)
            }
        }

        flavor?.values?.forEach { it.validate() }
        return true
    }

    fun mergeDefaults() {
        flavor?.forEach { (name, flavor) ->
            flavor.mergeWithDefault(default, name)
        }
    }
}

data class AnnaiWeb (
    var default: AnnaiWebDefaultApp = AnnaiWebDefaultApp(),
    var flavor: Map<String, AnnaiWebFlavor>? = null,
)
{
    val isValid: Boolean
        get()  {
            return if(flavor?.values?.isEmpty() == true) {
                AnnaiDataUtils.isValidId(default.id)
                        && AnnaiDataUtils.isValidVersionCode(default.version_code)
            } else {
                flavor?.values?.all { it.isValid } ?: false
            }
        }

    fun validate(): Boolean {
        if(flavor?.values?.isEmpty() == true) {
            if (!AnnaiDataUtils.isValidId(default.id)) {
                throwError("Web default app ID is missing!", IllegalArgumentException::class)
            }
            if (!AnnaiDataUtils.isValidVersionCode(default.version_code)) {
                throwError("Web default version_code is missing!", IllegalArgumentException::class)
            }
        }

        flavor?.values?.forEach { it.validate() }
        return true
    }

    fun mergeDefaults() {
        flavor?.forEach { (name, flavor) ->
            flavor.mergeWithDefault(default, name)
        }
    }
}

data class AnnaiWindows (
    var default: AnnaiWindowsDefaultApp = AnnaiWindowsDefaultApp(),
    var flavor: Map<String, AnnaiWindowsFlavor>? = null,
)
{
    val isValid: Boolean
        get()  {
            return if(flavor?.values?.isEmpty() == true) {
                AnnaiDataUtils.isValidId(default.id)
                        && AnnaiDataUtils.isValidVersionCode(default.version_code)
            } else {
                flavor?.values?.all { it.isValid } ?: false
            }
        }

    fun validate(): Boolean {
        if(flavor?.values?.isEmpty() == true) {
            if (!AnnaiDataUtils.isValidId(default.id)) {
                throwError("Windows default app ID is missing!", IllegalArgumentException::class)
            }
            if (!AnnaiDataUtils.isValidVersionCode(default.version_code)) {
                throwError("Windows default version_code is missing!", IllegalArgumentException::class)
            }
        }

        flavor?.values?.forEach { it.validate() }
        return true
    }

    fun mergeDefaults() {
        flavor?.forEach { (name, flavor) ->
            flavor.mergeWithDefault(default, name)
        }
    }
}

data class AnnaiAndroidDefaultApp (
    var id: String? = null,
    var name: String? = null,
    var version_name: String? = null,
    var version_code: Int? = null,
    var priority: Int = 0,
    var main_file: String? = null,
    var gms_ads_id: String? = null,
    var signature: AnnaiAndroidKeysData? = null,
    var firebase: AnnaiAndroidFirebaseData? = null,
    var in_app_subscription: List<AnnaiInAppSubscription>? = null,
    var fastlane: FastlaneAndroidData? = null,
)

data class AnnaiIosDefaultApp (
    var id: String? = null,
    var name: String? = null,
    var version_name: String? = null,
    var version_code: Int? = null,
    var main_file: String? = null,
    var apple_id: String? = null,
    var gms_ads_id: String? = null,
    var firebase: AnnaiIosFirebaseData? = null,
    var in_app_subscription: List<AnnaiInAppSubscription>? = null,
    var fastlane: FastlaneIosData? = null,
    )

data class AnnaiWindowsDefaultApp (
    var id: String? = null,
    var name: String? = null,
    var version_name: String? = null,
    var version_code: Int? = null,
    var main_file: String? = null,
    var firebase: AnnaiWindowsFirebaseData? = null,
    var in_app_subscription: List<AnnaiInAppSubscription>? = null,
)

data class AnnaiWebDefaultApp (
    var id: String? = null,
    var name: String? = null,
    var version_name: String? = null,
    var version_code: Int? = null,
    var main_file: String? = null,
    var firebase: AnnaiWebFirebaseData? = null,
    var in_app_subscription: List<AnnaiInAppSubscription>? = null,
)

data class AnnaiAndroidSdk(
    var minSdk: Int? = null,
    var compileSdk: Int? = null,
    var targetSdk: Int? = null,
)

data class AnnaiAndroidBuildType (
    var minifyEnabled: Boolean? = null,
    var shrinkResources: Boolean? = null,
    var ndkVersion: String? = null,
    var ndkDebugSymbolLevel: String? = null,
    var ndkAbiFilters: List<String>? = null,
    var lintCheckReleaseBuilds: Boolean? = null,
)

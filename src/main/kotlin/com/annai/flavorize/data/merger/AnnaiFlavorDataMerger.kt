package com.annai.flavorize.data.merger

import com.annai.flavorize.data.spec.app.AnnaiAndroidDefaultApp
import com.annai.flavorize.data.spec.app.AnnaiIosDefaultApp
import com.annai.flavorize.data.spec.app.AnnaiWebDefaultApp
import com.annai.flavorize.data.spec.app.AnnaiWindowsDefaultApp
import com.annai.flavorize.data.spec.app.firebase.AndroidFirebaseConfig
import com.annai.flavorize.data.spec.app.firebase.AnnaiAndroidFirebaseData
import com.annai.flavorize.data.spec.app.firebase.AnnaiIosFirebaseData
import com.annai.flavorize.data.spec.app.firebase.AnnaiWebFirebaseData
import com.annai.flavorize.data.spec.app.firebase.AnnaiWindowsFirebaseData
import com.annai.flavorize.data.spec.app.firebase.IosFirebaseConfig
import com.annai.flavorize.data.spec.app.firebase.WebFirebaseConfig
import com.annai.flavorize.data.spec.app.firebase.WindowsFirebaseConfig
import com.annai.flavorize.data.spec.app.flavor.AnnaiAndroidFlavor
import com.annai.flavorize.data.spec.app.flavor.AnnaiIosFlavor
import com.annai.flavorize.data.spec.app.flavor.AnnaiWebFlavor
import com.annai.flavorize.data.spec.app.flavor.AnnaiWindowsFlavor

// --- Flavor Mergers ---

fun AnnaiAndroidFlavor.mergeWithDefault(default: AnnaiAndroidDefaultApp, flavorName: String) {
    this.id = this.id ?: default.id
    this.id_suffix = this.id_suffix ?: ""
    this.name = this.name ?: default.name
    this.version_name = this.version_name ?: default.version_name
    this.version_code = this.version_code ?: default.version_code
    this.priority = this.priority ?: default.priority
    this.main_file = this.main_file ?: default.main_file
    this.gms_ads_id = this.gms_ads_id ?: default.gms_ads_id
    // Call the specific Android Firebase merger
    this.firebase = this.firebase?.mergeWithDefault(default.firebase) ?: default.firebase
    this.in_app_subscription = this.in_app_subscription ?: default.in_app_subscription
    this.flavorName = flavorName
}

fun AnnaiIosFlavor.mergeWithDefault(default: AnnaiIosDefaultApp, flavorName: String) {
    this.id = this.id ?: default.id
    this.id_suffix = this.id_suffix ?: ""
    this.name = this.name ?: default.name
    this.version_name = this.version_name ?: default.version_name
    this.version_code = this.version_code ?: default.version_code
    this.main_file = this.main_file ?: default.main_file
    this.apple_id = this.apple_id ?: default.apple_id
    this.gms_ads_id = this.gms_ads_id ?: default.gms_ads_id
    // Call the specific iOS Firebase merger
    this.firebase = this.firebase?.mergeWithDefault(default.firebase) ?: default.firebase
    this.in_app_subscription = this.in_app_subscription ?: default.in_app_subscription
    this.flavorName = flavorName
}

fun AnnaiWebFlavor.mergeWithDefault(default: AnnaiWebDefaultApp, flavorName: String) {
    this.id = this.id ?: default.id
    this.id_suffix = this.id_suffix ?: ""
    this.name = this.name ?: default.name
    this.version_name = this.version_name ?: default.version_name
    this.version_code = this.version_code ?: default.version_code
    this.main_file = this.main_file ?: default.main_file
    // Call the specific Web Firebase merger
    this.firebase = this.firebase?.mergeWithDefault(default.firebase) ?: default.firebase
    this.in_app_subscription = this.in_app_subscription ?: default.in_app_subscription
    this.flavorName = flavorName
}

fun AnnaiWindowsFlavor.mergeWithDefault(default: AnnaiWindowsDefaultApp, flavorName: String) {
    this.id = this.id ?: default.id
    this.id_suffix = this.id_suffix ?: ""
    this.name = this.name ?: default.name
    this.version_name = this.version_name ?: default.version_name
    this.version_code = this.version_code ?: default.version_code
    this.main_file = this.main_file ?: default.main_file
    // Call the specific Windows Firebase merger
    this.firebase = this.firebase?.mergeWithDefault(default.firebase) ?: default.firebase
    this.in_app_subscription = this.in_app_subscription ?: default.in_app_subscription
    this.flavorName = flavorName
}

// --- Platform-Specific Firebase Data Mergers ---

fun AnnaiAndroidFirebaseData?.mergeWithDefault(default: AnnaiAndroidFirebaseData?): AnnaiAndroidFirebaseData? {
    if (this == null) return default // If current is null, return default
    if (default == null) return this // If default is null, return current

    return AnnaiAndroidFirebaseData(
        release = this.release?.mergeWithDefault(default.release) ?: default.release,
        debug = this.debug?.mergeWithDefault(default.debug) ?: default.debug,
        profile = this.profile?.mergeWithDefault(default.profile) ?: default.profile
    )
}

fun AnnaiIosFirebaseData?.mergeWithDefault(default: AnnaiIosFirebaseData?): AnnaiIosFirebaseData? {
    if (this == null) return default
    if (default == null) return this

    return AnnaiIosFirebaseData(
        release = this.release?.mergeWithDefault(default.release) ?: default.release,
        debug = this.debug?.mergeWithDefault(default.debug) ?: default.debug,
        profile = this.profile?.mergeWithDefault(default.profile) ?: default.profile
    )
}

fun AnnaiWindowsFirebaseData?.mergeWithDefault(default: AnnaiWindowsFirebaseData?): AnnaiWindowsFirebaseData? {
    if (this == null) return default
    if (default == null) return this

    return AnnaiWindowsFirebaseData(
        release = this.release?.mergeWithDefault(default.release) ?: default.release,
        debug = this.debug?.mergeWithDefault(default.debug) ?: default.debug,
        profile = this.profile?.mergeWithDefault(default.profile) ?: default.profile
    )
}

fun AnnaiWebFirebaseData?.mergeWithDefault(default: AnnaiWebFirebaseData?): AnnaiWebFirebaseData? {
    if (this == null) return default
    if (default == null) return this

    return AnnaiWebFirebaseData(
        release = this.release?.mergeWithDefault(default.release) ?: default.release,
        debug = this.debug?.mergeWithDefault(default.debug) ?: default.debug,
        profile = this.profile?.mergeWithDefault(default.profile) ?: default.profile
    )
}

// --- Platform-Specific FirebaseConfig Mergers ---

fun AndroidFirebaseConfig?.mergeWithDefault(default: AndroidFirebaseConfig?): AndroidFirebaseConfig? {
    if (this == null) return default
    if (default == null) return this

    return AndroidFirebaseConfig(
        project_id = this.project_id ?: default.project_id,
        firebase_app_id = this.firebase_app_id ?: default.firebase_app_id,
        path = this.path ?: default.path
    )
}

fun IosFirebaseConfig?.mergeWithDefault(default: IosFirebaseConfig?): IosFirebaseConfig? {
    if (this == null) return default
    if (default == null) return this

    return IosFirebaseConfig(
        project_id = this.project_id ?: default.project_id,
        firebase_app_id = this.firebase_app_id ?: default.firebase_app_id,
        path = this.path ?: default.path,
        build_target = this.build_target ?: default.build_target // Include build_target for iOS
    )
}

fun WindowsFirebaseConfig?.mergeWithDefault(default: WindowsFirebaseConfig?): WindowsFirebaseConfig? {
    if (this == null) return default
    if (default == null) return this

    return WindowsFirebaseConfig(
        project_id = this.project_id ?: default.project_id,
        firebase_app_id = this.firebase_app_id ?: default.firebase_app_id
        // No 'path' for Windows
    )
}

fun WebFirebaseConfig?.mergeWithDefault(default: WebFirebaseConfig?): WebFirebaseConfig? {
    if (this == null) return default
    if (default == null) return this

    return WebFirebaseConfig(
        project_id = this.project_id ?: default.project_id,
        firebase_app_id = this.firebase_app_id ?: default.firebase_app_id
        // No 'path' for Web
    )
}
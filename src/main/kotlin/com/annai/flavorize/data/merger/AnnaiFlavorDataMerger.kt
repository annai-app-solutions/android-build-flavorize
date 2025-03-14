package com.annai.flavorize.data.merger

import com.annai.flavorize.data.spec.app.AnnaiIosDefaultApp
import com.annai.flavorize.data.spec.app.AnnaiWebDefaultApp
import com.annai.flavorize.data.spec.app.*
import com.annai.flavorize.data.spec.app.firebase.AnnaiFirebaseData
import com.annai.flavorize.data.spec.app.firebase.FirebaseConfig
import com.annai.flavorize.data.spec.app.flavor.AnnaiAndroidFlavor
import com.annai.flavorize.data.spec.app.flavor.AnnaiIosFlavor
import com.annai.flavorize.data.spec.app.flavor.AnnaiWebFlavor

fun AnnaiAndroidFlavor.mergeWithDefault(default: AnnaiAndroidDefaultApp, flavorName: String) {
    id = id ?: default.id
    id_suffix = id_suffix ?: ""
    name = name ?: default.name
    version_name = version_name ?: default.version_name
    version_code = version_code ?: default.version_code
    priority = priority ?: default.priority
    main_file = main_file ?: default.main_file
    gms_ads_id = gms_ads_id ?: default.gms_ads_id
    firebase = firebase?.mergeWithDefault(default.firebase) ?: default.firebase
    in_app_subscription = in_app_subscription ?: default.in_app_subscription
    this.flavorName = flavorName
}

fun AnnaiIosFlavor.mergeWithDefault(default: AnnaiIosDefaultApp, flavorName: String) {
    id = id ?: default.id
    id_suffix = id_suffix ?: ""
    name = name ?: default.name
    version_name = version_name ?: default.version_name
    version_code = version_code ?: default.version_code
    main_file = main_file ?: default.main_file
    apple_id = apple_id ?: default.apple_id
    gms_ads_id = gms_ads_id ?: default.gms_ads_id
    firebase = firebase?.mergeWithDefault(default.firebase) ?: default.firebase
    in_app_subscription = in_app_subscription ?: default.in_app_subscription
    this.flavorName = flavorName
}

fun AnnaiWebFlavor.mergeWithDefault(default: AnnaiWebDefaultApp, flavorName: String) {
    id = id ?: default.id
    id_suffix = id_suffix ?: ""
    name = name ?: default.name
    version_name = version_name ?: default.version_name
    version_code = version_code ?: default.version_code
    main_file = main_file ?: default.main_file
    firebase = firebase?.mergeWithDefault(default.firebase) ?: default.firebase
    in_app_subscription = in_app_subscription ?: default.in_app_subscription
    this.flavorName = flavorName
}

fun AnnaiFirebaseData?.mergeWithDefault(default: AnnaiFirebaseData?): AnnaiFirebaseData? {
    if (this == null) return default
    if (default == null) return this

    return AnnaiFirebaseData(
        release = this.release?.mergeWithDefault(default.release) ?: default.release,
        debug = this.debug?.mergeWithDefault(default.debug) ?: default.debug,
        profile = this.profile?.mergeWithDefault(default.profile) ?: default.profile
    )
}

fun FirebaseConfig?.mergeWithDefault(default: FirebaseConfig?): FirebaseConfig? {
    if (this == null) return default
    if (default == null) return this

    return FirebaseConfig(
        project_id = this.project_id ?: default.project_id,
        google_app_id = this.google_app_id ?: default.google_app_id,
        file = this.file ?: default.file
    )
}

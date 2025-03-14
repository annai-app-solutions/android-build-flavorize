package com.annai.flavorize.data.spec.app.inapp

data class AnnaiInAppSubscription (
    var apiKey: String? = null,
    var entitlementIds: List<String>? = null,
    var others: Map<String, String>? = null,
)
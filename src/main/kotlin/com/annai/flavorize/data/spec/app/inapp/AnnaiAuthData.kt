package com.annai.flavorize.data.spec.app.inapp

data class AnnaiAuthData (
    var release: AnnaiAuthConfigData? = null,
    var debug: AnnaiAuthConfigData? = null,
)

data class AnnaiAuthConfigData (
    var clientId: String? = null,
    var others: Map<String, String>? = null,
)
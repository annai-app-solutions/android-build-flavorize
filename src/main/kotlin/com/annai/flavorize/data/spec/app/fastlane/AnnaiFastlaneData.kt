package com.annai.flavorize.data.spec.app.fastlane


data class FastlaneAndroidData (
    var google_api_key: String? = null,
)

data class FastlaneIosData (
    var apple_api_key: String? = null,
    var export_options_plist: String? = null,
    var export_options_team_id: String? = null,
    var export_options_signing_certificate: String? = null,
)

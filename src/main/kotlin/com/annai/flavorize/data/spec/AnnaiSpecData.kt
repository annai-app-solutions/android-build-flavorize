package com.annai.flavorize.data.spec

import com.annai.flavorize.data.spec.app.AnnaiAppData

data class AnnaiSpecData(
    var enabled: Boolean = true,
    var annai_app: AnnaiAppData = AnnaiAppData(),
    var debug: AnnaiDebugData = AnnaiDebugData(),
)

data class AnnaiDebugData(
    var printDebug: Boolean = false,
    var printBuildAndFlavorInfo: Boolean = true,
    var printSdkVersions: Boolean = true,
    var printReleaseBuildTypeInfo: Boolean = false,
)

data class AnnaiPubSpecConfig(
    var name: String? = null,
    var versionName: String? = null,
    var versionCode: Int? = 0,
)

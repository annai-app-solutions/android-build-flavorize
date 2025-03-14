package com.annai.flavorize.spec

class AnnaiDataUtils {
    companion object {

        fun isValidId(id: String?) : Boolean{
            return !id.isNullOrBlank()
        }

        fun isValidVersionCode(versionCode: Int?) : Boolean{
            return versionCode != null && versionCode > 0
        }

        fun isValidPriority(priority: Int?) : Boolean{
            return priority != null && priority >= 1 && priority <= 5
        }
    }
}

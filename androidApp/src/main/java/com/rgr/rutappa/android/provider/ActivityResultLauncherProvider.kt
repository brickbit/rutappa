package com.rgr.rutappa.android.provider

import androidx.activity.result.ActivityResultLauncher

class ActivityResultLauncherProvider {

    private var providedActivityResultLauncher: ActivityResultLauncher<Array<String>>? = null

    fun setActivityResultLauncher(resultLauncher: ActivityResultLauncher<Array<String>>) {
        providedActivityResultLauncher = resultLauncher
    }

    fun getActivityResultLauncher(): ActivityResultLauncher<Array<String>>? {
        return providedActivityResultLauncher
    }
}
package com.rgr.rutappa.android.provider

import android.app.Activity

class ActivityProvider {

    private var providedActivity: Activity? = null

    fun setActivity(activity: Activity) {
        providedActivity = activity
    }

    fun getActivity(): Activity? {
        return providedActivity
    }
}
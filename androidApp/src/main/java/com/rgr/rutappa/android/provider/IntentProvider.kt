package com.rgr.rutappa.android.provider

import android.content.Intent

class IntentProvider {

    private var providedIntent: Intent? = null

    fun setIntent(intent: Intent?) {
        providedIntent = intent
    }

    fun getIntent(): Intent? {
        return providedIntent
    }
}
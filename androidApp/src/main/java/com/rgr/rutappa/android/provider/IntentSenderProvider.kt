package com.rgr.rutappa.android.provider

import android.content.IntentSender

class IntentSenderProvider {

    private var providedIntentSender: IntentSender? = null

    fun setIntentSender(intent: IntentSender?) {
        providedIntentSender = intent
    }

    fun getIntentSender(): IntentSender? {
        return providedIntentSender
    }
}
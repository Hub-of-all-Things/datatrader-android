package com.hubofallthings.datatrader.helper

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.support.customtabs.CustomTabsIntent

class ChromeHelper(private val activity: Activity) {

    fun startBrowser(url: String) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        try {
            customTabsIntent.launchUrl(activity, Uri.parse(url))
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            // todo webview
        }
    }
}
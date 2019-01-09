package com.hubofallthings.dataplugs.helpers

import android.content.Context
import android.net.ConnectivityManager


class HATNetworkHelper(private val context: Context){
    fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }
}

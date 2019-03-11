package com.hubofallthings.datatrader.service

import android.content.Context

class DataTraderPreference(private val context: Context) {
    private val PREFERENCE_NAME = "SharedPreference"
    private val PREFERENCE_TOKEN = "token"
    private val PREFERENCE_USERDOMAIN = "userdomain"
    private val PREFERENCE_LOGIN = "login"

    val pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun deletePreference() {
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit().clear().apply()
    }

    fun getToken(): String {
        return pref.getString(PREFERENCE_TOKEN, "")
    }

    fun setToken(token: String?) {
        val editor = pref.edit()
        editor.putString(PREFERENCE_TOKEN, token)
        editor.apply()
    }

    fun getUserDomain(): String {
        return pref.getString(PREFERENCE_USERDOMAIN, "")
    }

    fun setUserDomain(userDomain: String?) {
        val editor = pref.edit()
        editor.putString(PREFERENCE_USERDOMAIN, userDomain)
        editor.apply()
    }

    fun getLoginStatus(): Boolean {
        return pref.getBoolean(PREFERENCE_LOGIN, false)
    }

    fun setLoginStatus(status: Boolean) {
        val editor = pref.edit()
        editor.putBoolean(PREFERENCE_LOGIN, status)
        editor.apply()
    }
}
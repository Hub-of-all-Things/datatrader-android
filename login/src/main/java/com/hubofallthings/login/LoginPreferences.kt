package com.hubofallthings.login

import android.content.Context

class LoginPreferences(private val context : Context){
    val PREFERENCE_NAME = "LoginPreference"
    val PREFERENCE_HATDOMAIN = "hatdomain"
    val PREFERENCE_USERDOMAIN = "userdomain"
    val PREFERENCE_HATNAME = "hatname"


    val pref = context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE)

    fun getHATDomain() : String?{
        return pref.getString(PREFERENCE_HATDOMAIN,"")
    }
    fun setHATDomain(hatDomain:String?){
        val editor = pref.edit()
        editor.putString(PREFERENCE_HATDOMAIN,hatDomain)
        editor.apply()
    }
    fun getHATName() : String?{
        return pref.getString(PREFERENCE_HATNAME,"")
    }
    fun setHATName(hatName:String?){
        val editor = pref.edit()
        editor.putString(PREFERENCE_HATNAME,hatName)
        editor.apply()
    }
    fun getUserDomain(): String?{
        return pref.getString(PREFERENCE_USERDOMAIN,"")
    }
    fun setUserDomain(userDomain : String?){
        val editor = pref.edit()
        editor.putString(PREFERENCE_USERDOMAIN,userDomain)
        editor.apply()
    }
}
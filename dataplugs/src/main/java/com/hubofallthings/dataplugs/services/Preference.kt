package com.hubofallthings.dataplugs.services

import android.content.Context

class Preference(context: Context) {
    val PREFERENCE_NAME = "dataplugsPreferences"
    val PREFERENCE_TOKEN = "token"
    val PREFERENCE_LOGIN = "login"
    val PREFERENCE_USERDOMAIN = "userdomain"
    val PREFERENCE_HATNAME = "hatname"
    val PREFERENCE_IMAGEURL = "imageurl"
    val PREFERENCE_HATDOMAIN = "hatdomain"
    val PREFERENCE_NEWDATAPLUG = "dataplug"


    val PREFERENCE_URL = "url"

    val pref = context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE)


    fun deletePreference(context: Context){
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit().clear().apply()
    }
    fun getToken():String{
        return pref.getString(PREFERENCE_TOKEN,"")
    }
    fun setToken(token : String?){
        val editor = pref.edit()
        editor.putString(PREFERENCE_TOKEN,token)
        editor.apply()
    }
    fun getUrl():String{
        return pref.getString(PREFERENCE_URL,"")
    }
    fun setUrl(url : String?){
        val editor = pref.edit()
        editor.putString(PREFERENCE_URL,url)
        editor.apply()
    }
    fun getUserDomain(): String{
        return pref.getString(PREFERENCE_USERDOMAIN,"")
    }
    fun setUserDomain(userDomain : String?){
        val editor = pref.edit()
        editor.putString(PREFERENCE_USERDOMAIN,userDomain)
        editor.apply()
    }

    fun getLoginStatus() : Boolean{
        return pref.getBoolean(PREFERENCE_LOGIN,false)
    }
    fun setLoginStatus(status:Boolean){
        val editor = pref.edit()
        editor.putBoolean(PREFERENCE_LOGIN,status)
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
    fun getHATDomain() : String?{
        return pref.getString(PREFERENCE_HATDOMAIN,"")
    }
    fun setHATDomain(hatDomain:String?){
        val editor = pref.edit()
        editor.putString(PREFERENCE_HATDOMAIN,hatDomain)
        editor.apply()
    }
    fun getImageUrl() : String?{
        return pref.getString(PREFERENCE_IMAGEURL,"")
    }
    fun setImageUrl(imageUrl:String?){
        val editor = pref.edit()
        editor.putString(PREFERENCE_IMAGEURL,imageUrl)
        editor.apply()
    }
    fun setUrlDataPlug(url:String?){
        val editor = pref.edit()
        editor.putString(PREFERENCE_NEWDATAPLUG,url)
        editor.apply()
    }
    fun getUrlDataPlug() : String?{
        return pref.getString(PREFERENCE_NEWDATAPLUG,"")
    }
    fun getCacheExpirationDate(prefereneID : String):Long{
        return pref.getLong(prefereneID,0L)
    }
    fun setCacheExpirationDate(untilDate : Long,prefereneID : String) {
        val editor = pref.edit()
        editor.putLong(prefereneID,untilDate)
        editor.apply()
    }
}
package com.hubofallthings.login

import android.app.Activity
import android.widget.Button

class HATDomainServices(private val activity : Activity){
    val mPreference = LoginPreferences(activity)

    fun setDomain(hatDomain : String?) {
        mPreference.setHATDomain(hatDomain)
    }
    fun getDomain() : String?{
        return mPreference.getHATDomain()
    }
    fun checkBuildType(){
        if (!BuildConfig.BUILD_TYPE.contentEquals("release")) {
            val dom1 = activity.findViewById<Button>(R.id.domainOpt1)
            val dom3 = activity.findViewById<Button>(R.id.domainOpt3)
            dom1.isEnabled = false
            dom3.isEnabled = false
        }
    }
}
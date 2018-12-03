package com.hubofallthings.datatrader.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.hubofallthings.datatrader.activity.MainActivity
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class SettingsServices{
    fun logout(activity: Activity?){
        if(activity!=null){
            EncryptionServices(activity).removeMasterKey()
            DataTraderPreference(activity).deletePreference()
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
            activity.finishAffinity()
        }
    }
}
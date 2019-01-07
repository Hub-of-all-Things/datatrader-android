package com.hubofallthings.datatrader.service

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.hubofallthings.datatrader.activity.MainActivity
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class SettingsServices{
    private val packageName = "com.hubofallthings.hatappandroid"
    fun logout(activity: Activity?){
        if(activity!=null){
            EncryptionServices(activity).removeMasterKey()
            DataTraderPreference(activity).deletePreference()
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
            activity.finishAffinity()
        }
    }
    fun startNewAppOrMarket(activity: Activity?) {
        var intent = activity?.packageManager?.getLaunchIntentForPackage(packageName)
        if (intent == null) {
            // Bring user to the market or let them choose an app?
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("market://details?id=$packageName")
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            activity?.startActivity(intent)
        } catch (e : ActivityNotFoundException){ }
    }
    fun sendEmail(activity: Activity?) {
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:contact@hatdex.org")
        try {
            activity?.startActivity(emailIntent)
        } catch (e : ActivityNotFoundException){
            Toast.makeText(activity,"We can’t connect to your mail app, please contact us at contact@hatdex.org", Toast.LENGTH_SHORT).show()
        }
    }
}
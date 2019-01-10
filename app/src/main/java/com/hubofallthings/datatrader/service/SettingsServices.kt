package com.hubofallthings.datatrader.service

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.hubofallthings.dataplugs.activity.DataPlugsActivity
import com.hubofallthings.datatrader.activity.MainActivity
import com.hubofallthings.datatrader.activity.ResetPasswordActivity
import com.hubofallthings.datatrader.helper.UserHelper
import com.hubofallthings.login.BuildConfig
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import kotlin.contracts.contract

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
    fun openDataPlugs(activity: Activity){
        val userHelper = UserHelper(activity)
        val userDomain = userHelper.getUserDomain()
        val token = userHelper.getToken()
        val intent = Intent(activity, DataPlugsActivity::class.java)
        intent.putExtra("userDomain",userDomain)
        intent.putExtra("userToken",token)
        activity.startActivity(intent)
    }
    fun getIssuer() : String {
        return if(BuildConfig.BUILD_TYPE.contentEquals("release")) {
            "HAT Data Exchange Ltd."
        }else {
            "HATLAB"
        }
    }
    fun getVendor() : String {
        return if(BuildConfig.BUILD_TYPE.contentEquals("release")) {
            "DataTraderGive"
        }else {
            "DataTraderGive Testing"
        }
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
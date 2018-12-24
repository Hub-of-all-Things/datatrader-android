package com.hubofallthings.datatrader.service

import android.app.Activity
import android.util.Log
import com.hubofallthings.android.hatApi.services.HATAccountService
import com.hubofallthings.datatrader.helper.UserHelper

class HATPasswordServices(private val activity : Activity){
    private val mUserHelper = UserHelper(activity)

    fun networkManager(){
        mUserHelper.getUserDomain()
    }
    fun resetPasswordManager(email : String){
        resetPassword(mUserHelper.getUserDomain(),email)
    }
    private fun resetPassword(userDomain : String , email : String){
        HATAccountService().resetPassword(userDomain,email,{
            //success
            SettingsServices().logout(activity)
            Log.i("ResetPassword","success")
        },{
            //failed
            Log.i("ResetPassword","Msg : Fail "+ it.errorMessage + " status code : " +it.errorCode.toString())
        })
    }
}
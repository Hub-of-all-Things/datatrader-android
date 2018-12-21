package com.hubofallthings.datatrader.service

import android.app.Activity
import android.widget.Toast
import com.hubofallthings.android.hatApi.HATError
import com.hubofallthings.android.hatApi.objects.extrernalapps.HATApplicationObject
import com.hubofallthings.android.hatApi.services.HATExternalAppsService
import com.hubofallthings.datatrader.helper.UserHelper

class EnableDataTraderApp(private val activity : Activity){
    private val mUserHelper = UserHelper(activity)
    fun enableDataTrader(successFulCallback: ((HATApplicationObject?, String?) -> Unit),failCallBack: ((HATError) -> Unit)){
        val token = mUserHelper.getToken()
        val userDomain = mUserHelper.getUserDomain()
        if(!token.isNullOrEmpty())
        HATExternalAppsService().setUpApp(token,userDomain,"databuyerstaging",{app, newToken -> successFulCallback(app,newToken)},{error -> failCallBack(error)})
    }

}
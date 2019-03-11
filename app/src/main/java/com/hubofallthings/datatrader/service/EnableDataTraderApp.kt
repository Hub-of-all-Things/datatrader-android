package com.hubofallthings.datatrader.service

import android.app.Activity
import android.util.Log
import com.hubofallthings.android.hatApi.HATError
import com.hubofallthings.android.hatApi.objects.extrernalapps.HATApplicationObject
import com.hubofallthings.android.hatApi.services.HATExternalAppsService
import com.hubofallthings.datatrader.helper.UserHelper
import com.hubofallthings.login.BuildConfig

class EnableDataTraderApp(private val activity: Activity) {
    private val mUserHelper = UserHelper(activity)
    fun enableDataTrader(
        successFulCallback: ((HATApplicationObject?, String?) -> Unit),
        failCallBack: ((HATError) -> Unit)
    ) {
        val token = mUserHelper.getToken()
        val userDomain = mUserHelper.getUserDomain()
        if (!token.isNullOrEmpty())
            HATExternalAppsService().setUpApp(
                token,
                userDomain,
                "databuyerstaging",
                { app, newToken -> successFulCallback(app, newToken) },
                { error -> failCallBack(error) })
    }

    fun checkDataTraderUpdates() {
        Log.i("datatraderupdates", "checkHATAppUpdates")
        val token = mUserHelper.getToken()
        val userDomain = mUserHelper.getUserDomain()
        val appName = if (BuildConfig.BUILD_TYPE.contentEquals("release")) {
            "datatrader"
        } else {
            "datatraderstaging"
        }
        if (token != null) {
            HATExternalAppsService().getAppWithAppId(token, userDomain, appName, { app, _ ->
                app?.let {
                    if (it.needsUpdating != null) {
                        if (it.needsUpdating!!) {
                            // needs update logout
                            mUserHelper.goForLogin()
                        } else {
                            // false
                        }
                    }
                }
            }, { error ->
                error.errorCode
            })
        }
    }
}
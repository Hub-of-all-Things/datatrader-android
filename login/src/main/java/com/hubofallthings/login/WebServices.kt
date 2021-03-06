package com.hubofallthings.login

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.widget.Toast
import com.hubofallthings.android.hatApi.HATError

class WebServices(private var activity: Activity) {
    var urlScheme:String = "databuyer"
    var serviceName:String = "databuyer"
    var localAuthHost:String = "databuyerhost"

    //success result from login to HAT , go to DrawerActivity
    fun signInSuccess (userDomain : String? , newToken : String?){
        val resultIntent = Intent()
        resultIntent.putExtra("newToken", newToken)
        resultIntent.putExtra("userDomain", userDomain)

        activity.setResult(RESULT_OK,resultIntent).also {
            activity.finish()
        }
    }

    // failed result from login to HAT , go to LoginActivity
    fun signInFail (error : HATError) {
        Toast.makeText(activity, error.errorMessage, Toast.LENGTH_LONG).show()
        activity.setResult(RESULT_CANCELED).also { activity.finish() }
    }
    fun getHatLoginURL(userDomain: String): String {
        serviceName = getServiceNameValue()
        return "https://$userDomain/#/hatlogin?name=$serviceName&redirect=$urlScheme://$localAuthHost&fallback=databuyer://loginfailed"
    }
    fun getServiceNameValue() : String {
        return if(BuildConfig.BUILD_TYPE.contentEquals("release")) {
            "databuyer"
        }else {
            "databuyer"
        }
    }

}
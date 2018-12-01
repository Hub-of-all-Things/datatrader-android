package com.hubofallthings.login

import android.app.Activity
import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.result.Result

class LoginServices(private val activity: Activity){
    val myPreference = LoginPreferences(activity)

    fun setHATName(hatName : String?){
        myPreference.setHATName(hatName)
    }
    fun getHATName() : String?{
        return myPreference.getHATName()
    }
    fun setUserDomain(userDomain: String?){
        myPreference.setUserDomain(userDomain)
    }
    fun getUserDomain() : String? {
        return myPreference.getUserDomain()
    }

    fun validateHATPublicKey (address : String , succesfulCallBack:  (String, String?) -> Unit , failCallBack:  (error: String) -> Unit ){
        val url = "https://$address/publickey"
        Fuel.get(url).responseJson{ _, response, result ->
            when (result){
                is Result.Failure -> {
                    failCallBack("Invalid Username or Domain.")
                    Log.i("usernameFail" , response.statusCode.toString())
                }
                is Result.Success -> {
                    if(response.statusCode == 200){
                        Log.i("usernameSuccess" , response.statusCode.toString())
                        succesfulCallBack("valid address","")
                    }
                }
            }
        }
    }
}
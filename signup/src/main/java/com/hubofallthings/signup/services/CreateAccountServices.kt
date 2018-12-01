package com.hubofallthings.signup.services

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.hubofallthings.android.hatApi.HATError
import com.hubofallthings.android.hatApi.objects.purchase.PurchaseMembershipObject
import com.hubofallthings.android.hatApi.objects.purchase.PurchaseObject
import com.hubofallthings.android.hatApi.services.HATService
import com.hubofallthings.markdown.activity.MarkDownActivity
import com.hubofallthings.signup.BuildConfig
import com.hubofallthings.signup.activity.CreateAccountActivity
import com.hubofallthings.signup.activity.CreateAccountFinal
import com.hubofallthings.signup.activity.CreateAccountOptins
import com.hubofallthings.signup.activity.CreateConfirmationActivity
import com.hubofallthings.signup.activity.CreatePasswordActivity
import com.hubofallthings.signup.activity.CreateUsernameActivity
import com.hubofallthings.signup.objects.CreateAccountObject
import java.util.regex.Pattern



class CreateAccountServices(private val activity: Activity) {
    var executed = false

    fun isEmailValid(email: String): Boolean {
        val expression = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{1,64}"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }
    //start Username Activity
    fun startUserNameActivity(mCreateObject : CreateAccountObject,backButton : Boolean){
        val intent = Intent(activity, CreateUsernameActivity::class.java)
        intent.putExtra("backbutton", backButton)
        intent.putExtra("createobject",mCreateObject)
        activity.startActivity(intent)
        activity.overridePendingTransition(0, 0)
        activity.finish()
    }
    //start Password Activity
    fun startPasswordActivity(mCreateObject: CreateAccountObject){
        val intent = Intent(activity, CreatePasswordActivity::class.java)
        intent.putExtra("key", "CreateAccount")
        intent.putExtra("createobject",mCreateObject)
        activity.startActivity(intent)
        activity.overridePendingTransition(0, 0)
        activity.finish()
    }
    //start MarkDown Activity
    fun startMarkDownActivity(context : Context, link :String){
        val intent = Intent(context, MarkDownActivity::class.java)
        intent.putExtra("key", "CreateConfirmation")
        intent.putExtra("link", link)
        activity.startActivity(intent)
    }
    //start Confirmation Activity
    fun startConfirmationActivity(mCreateObject: CreateAccountObject){
        val intent = Intent(activity, CreateConfirmationActivity::class.java)
        intent.putExtra("key", "CreateAccount")
        intent.putExtra("createobject", mCreateObject)
        activity.startActivity(intent)
        activity.finish()
    }
    //start Confirmation Activity
    fun startOptinsActivity(mCreateObject: CreateAccountObject){
        val intent = Intent(activity, CreateAccountOptins::class.java)
        intent.putExtra("key", "CreateAccount")
        intent.putExtra("createobject", mCreateObject)
        activity.startActivity(intent)
        activity.overridePendingTransition(0, 0)
        activity.finish()
    }
    //start CreateFinal Activity
    fun startCreateFinalActivity(mCreateObject: CreateAccountObject){
        val intent = Intent(activity, CreateAccountFinal::class.java)
        intent.putExtra("key", "Confirmation")
        intent.putExtra("createobject",mCreateObject)
        activity.startActivity(intent)
        activity.finish()
    }
    //start Login Activity
    fun startLoginActivity(){
        activity.finish()
    }
    //start Main Activity
    fun startMainActivity(){
        activity.finish()
    }
    //start Create Activity
    fun startCreateAccountActivity(mCreateObject : CreateAccountObject){
        val intent = Intent(activity, CreateAccountActivity::class.java)
        intent.putExtra("createobject",mCreateObject)
        intent.putExtra("backbutton", true)
        activity.startActivity(intent)
        activity.overridePendingTransition(0, 0)
        activity.finish()
    }
    private fun getPurchaseObject(mCreateObject: CreateAccountObject) : PurchaseObject {
        val firstName = mCreateObject.firstName
        val lastName = mCreateObject.lastName
        val email = mCreateObject.email
        val userName = mCreateObject.userName
        val password = mCreateObject.password
        val optins = mCreateObject.optins
        return PurchaseObject(firstName!!,true,lastName!!,email!!,userName!!,password!!,
            getCluster(),"United Kingdom", PurchaseMembershipObject("trial","sandbox"),optins,getAppId())
    }
    private fun getAppId(): String{
        return if(BuildConfig.BUILD_TYPE.contentEquals("release")) {
            "databuyer"
        }else {
            "databuyer"
        }
    }
    fun getCluster():String{
        return if(BuildConfig.BUILD_TYPE.contentEquals("release")) {
            "hat.direct"
        }else {
            "hubat.net"
        }
    }
    fun createAccount(mCreateObject: CreateAccountObject , successfulCallback: (result :  String,String?) -> Unit,failCallBack: (result :  HATError) -> Unit ) {
        if (! executed) {
            val purchaseObj = getPurchaseObject(mCreateObject)
            HATService().confirmHATPurchase(purchaseObj,{s1,s2 -> successfulCallback(s1,s2)},{e1->failCallBack(e1)})
            executed = true
        }
    }
    fun validateHAT (address : String , cluster : String , succesfulCallBack:  (String, String?) -> Unit , failCallBack:  (error: String) -> Unit ){
        HATService().validateHATAddress(address,cluster,{s1,s2 -> succesfulCallBack(s1,s2)},{e1->failCallBack(e1)})
    }

    fun validateEmailAddress (email: String , cluster: String , succesfulCallBack:  (String, String?) -> Unit , failCallBack:  (error: String) -> Unit) {
        HATService().validateEmailAddress(email,cluster,{s1,s2 -> succesfulCallBack(s1,s2)},{e1->failCallBack(e1)})
    }
}
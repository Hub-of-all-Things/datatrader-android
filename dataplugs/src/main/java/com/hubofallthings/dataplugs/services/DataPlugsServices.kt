package com.hubofallthings.dataplugs.services

import android.app.Activity
import android.content.Intent
import android.os.Parcelable
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.LinearLayout
import android.widget.ListView
import com.hubofallthings.android.hatApi.HATError
import com.hubofallthings.android.hatApi.objects.extrernalapps.HATApplicationObject
import com.hubofallthings.dataplugs.adapter.DataPlugsAdapter
import com.hubofallthings.dataplugs.R
import com.hubofallthings.dataplugs.helpers.UserHelper

class DataPlugsServices(private val activity: Activity){
    val mPreference = Preference(activity)
    var dataPlugsList : List<HATApplicationObject>? = null
    var state: Parcelable? = null
    private val mUserHelper = UserHelper(activity)
    private var appName : String = ""
    var snackbar :Snackbar? = null
    private val mEncryptionServices = EncryptionServices(activity)
    private val mAppsListView = activity.findViewById<LinearLayout>(R.id.dataPlugs_list)


    fun networkManager(stateList : Parcelable?, appNameReq : String){
        state = stateList
        appName = appNameReq
        getApps()
    }
    /*
    get all the data plugs
     */
    private fun getApps(){
        showSnackBar()
        val token = getToken()
        val userDomain = getUserDomain()
        if(token!=null)
            ExternalAppsServices().getApps(appName,token,userDomain,{ list: List<HATApplicationObject>?, s: String? -> completionDataPlugs(list,s)},{ error -> failcallback(error) })
    }
    /*
    adds dataplugs into the listview
     */
    private fun completionDataPlugs(appsList: List<HATApplicationObject>?, newToken: String?){
        dataPlugsList = appsList
        generateDataList(appsList)
        encryptToken(newToken)
        snackbar?.dismiss()
        mAppsListView.visibility = View.VISIBLE
    }
    private fun failcallback(error: HATError) {
        snackbar?.dismiss()
        if(error.errorCode == 401){
            goForLogin()
        }
    }
    //encrypt the newToken and store it to Preference
    private fun encryptToken ( token : String?) {
        token?.let {
            mEncryptionServices.createMasterKey(null)
            val encryptedToken = mEncryptionServices.encrypt(token, null)
            mPreference.setToken(encryptedToken)
        }
    }

    private fun showSnackBar(){
        val msg = if(appName == "DataPlug"){
            "Fetching data plugs, please wait"
        } else {
            "Fetching apps, please wait"
        }
        snackbar = Snackbar.make(activity.findViewById(R.id.dataplugs_layout),msg , Snackbar.LENGTH_INDEFINITE)
        snackbar?.show()

    }
    private fun goForLogin(){
        mPreference.setLoginStatus(false)
//        val intent = Intent(activity, MainActivity::class.java)
//        activity.startActivity(intent)
        activity.finish()
    }

    fun onDestroy(){
        snackbar?.dismiss()
    }

    fun disableApp(appID: String, completionDisable : ((HATApplicationObject?, String?)-> Unit)){
        val userDomain = getUserDomain()
        val token = getToken()
        if(token!=null){
            ExternalAppsServices()
                .disableApp(appID,token,userDomain,{ app, newToken -> completionDisable(app,newToken)},{ error -> failcallback(error)})
        } else {
            goForLogin()
        }
    }
    fun getAppInfo(appID: String, completionAppInfo : ((HATApplicationObject?, String?)-> Unit)){
        val userDomain = getUserDomain()
        val token = getToken()
        if(token!=null){
            ExternalAppsServices()
                .getAppInfo(appID,token,userDomain,{ app, newToken -> completionAppInfo(app,newToken)},{ error -> failcallback(error)})
        } else {
            goForLogin()
        }
    }
    fun getApp(appID: String?, completionGetApp : (HATApplicationObject?) -> Unit, failCallBack : (HATError) -> Unit){
        val userDomain = getUserDomain()
        val token = getToken()

        if(token!=null && appID != null){
            ExternalAppsServices()
                .getAppInfo(appID,token,userDomain,{ list, _ -> completionGetApp(list)}, { error -> failCallBack(error)})
        }
    }
    private fun generateDataList(dataPlugsList :  List<HATApplicationObject>?){
        if(dataPlugsList != null) {
            val adapter = DataPlugsAdapter(activity, dataPlugsList)
            val list = activity.findViewById<ListView>(R.id.dataPlugListView)
            if(list != null){
                list.adapter = adapter
                adapter.notifyDataSetChanged()
                if(state != null) {
                    list.onRestoreInstanceState(state)
                }
            }

        }
    }
    fun getLoginStatus() : Boolean{
        return mPreference.getLoginStatus()
    }
    private fun getToken() : String?{
        return mUserHelper.getToken()
    }
    fun getUserDomain() : String{
        return  mUserHelper.getUserDomain()
    }
}

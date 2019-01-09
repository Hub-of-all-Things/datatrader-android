package com.hubofallthings.dataplugs.services

import android.app.Activity
import android.content.Intent
import android.support.design.widget.Snackbar
import android.widget.ListView
import com.hubofallthings.android.hatApi.HATError
import com.hubofallthings.android.hatApi.objects.tools.HATToolsObject
import com.hubofallthings.android.hatApi.services.HATToolsService
import com.hubofallthings.dataplugs.helpers.UserHelper


class ToolsServices(private val activity: Activity){
    val mPreference = Preference(activity)
    var toolsList : List<HATToolsObject>? = null
    private val mUserHelper = UserHelper(activity)
    var snackbar :Snackbar? = null

    fun getLoginStatus() : Boolean{
        return mPreference.getLoginStatus()
    }
    private fun getToken() : String?{
        return mUserHelper.getToken()
    }
    fun getUserDomain() : String{
        return  mPreference.getUserDomain()
    }
    fun networkManager(){
//        ExecuteToolsTask().execute()
//        snackbar = Snackbar.make(activity.findViewById(R.id.drawer_layout), "Fetching tools, please wait", Snackbar.LENGTH_INDEFINITE) //todo tools msg
//        snackbar?.show()
        val userToken = getToken()
        val userDomain = getUserDomain()
        if(userToken != null){
            HATToolsService().getAvailableTools(userToken,userDomain, {list ,_ -> completion(list)},{})
        }
    }
    private fun completion(tools: List<HATToolsObject>?){
        generateToolsList(tools)
        toolsList = tools
        snackbar?.dismiss()
    }
    private fun goForLogin(){
//        mPreference.setLoginStatus(false)
//        val intent = Intent(activity, MainActivity::class.java)
//        activity.startActivity(intent)
        activity.finish()
    }

    private fun generateData(toolsJson : List<HATToolsObject>): ArrayList<HATToolsObject> {
        val result = ArrayList<HATToolsObject>()
        for(i in toolsJson.indices) {
            if (toolsJson[i].status.available){
                result.add(toolsJson[i])
            }
        }
        return result
    }

    fun onDestroy(){
        if(snackbar !=null){
            snackbar?.dismiss()
        }
    }

    fun enableTool(toolID: String,completionEnable: (HATToolsObject?) -> Unit){
        val token = getToken()
        val userDomain = getUserDomain()

        if(token != null && getLoginStatus()){
            HATToolsService().enableTool(toolID,token,userDomain,{tool , _ -> completionEnable(tool)},{error -> failCallBack(error)})
        } else {
            goForLogin()
        }
    }
    fun triggerTool(toolID: String ,completionTrigger: () -> Unit){
        val token = getToken()
        val userDomain = getUserDomain()
        if(token != null && getLoginStatus()){
            HATToolsService().triggerToolUpdate(toolID,token,userDomain,{_ , _ -> completionTrigger()},{error -> failCallBack(error)})
        } else {
            goForLogin()
        }
    }
    fun failCallBack(error: HATError?){
        if(error?.errorCode == 401){
            goForLogin()
        }
    }
    fun disableTool(toolID: String, completionDisable : (HATToolsObject?) -> Unit){
        val userDomain = getUserDomain()
        val token = getToken()
        if(token != null && getLoginStatus()){
            HATToolsService().disableTool(toolID,token,userDomain,{tool , _ -> completionDisable(tool)},{error -> failCallBack(error)})
        } else {
            goForLogin()
        }
    }
    fun getTool(toolID: String, completionGetTool : (HATToolsObject?) -> Unit,failCallBack : (HATError?) -> Unit){
        val userDomain = getUserDomain()
        val token = getToken()
        if(token != null){
            HATToolsService().getTool(toolID,token,userDomain,{tool , _ -> completionGetTool(tool)},{error -> failCallBack(error)})
        } else {
            goForLogin()
        }
    }
    private fun generateToolsList(toolsList :  List<HATToolsObject>?){
        if(toolsList != null) {
//            val adapter = ToolsAdapter(activity, generateData(toolsList)) //todo tools adapter
//            val list = activity.findViewById<ListView>(R.id.tools_list_view)
//            if(list != null){
//                list.adapter = adapter
//                adapter.notifyDataSetChanged()
//            }

        }
    }
}

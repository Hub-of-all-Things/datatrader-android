package com.hubofallthings.dataplugs.activity

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ListView
import android.widget.TextView
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.android.core.Json
import com.hubofallthings.android.hatApi.managers.HATNetworkManager
import com.hubofallthings.android.hatApi.managers.ResultType
import com.hubofallthings.android.hatApi.objects.extrernalapps.HATApplicationObject
import com.hubofallthings.android.hatApi.objects.tools.HATToolsObject
import com.hubofallthings.dataplugs.R
import com.hubofallthings.dataplugs.adapter.DataPreviewFeedAdapter
import com.hubofallthings.dataplugs.helpers.DataPlugsDetailsHelper
import com.hubofallthings.dataplugs.helpers.HATDateHelper
import com.hubofallthings.dataplugs.helpers.ToolsDetailsHelper
import com.hubofallthings.dataplugs.helpers.UserHelper
import com.hubofallthings.dataplugs.objects.FeedItem
import com.hubofallthings.dataplugs.objects.ProfileInfoDataPreviewObject
import com.hubofallthings.dataplugs.services.Preference
import android.content.Intent
import android.content.ActivityNotFoundException
import android.net.Uri
import android.widget.Toast


class DataPreviewActivity : AppCompatActivity(){
    private var staticInfoFlag : Boolean = false
    private var feedPreviewFlag : Boolean = false
    private var hasStaticInfo : Boolean = false
    private var snackbar : Snackbar? = null
    var url = ""
    private var token : String? = null
    var listView : ListView? = null
    var feedList :  List<FeedItem>? = null
    var endPoint : String? = null
    lateinit var listHeader : View
    var dataPlug : HATApplicationObject? = null
    lateinit var mDataPlugsDetailsHelper : DataPlugsDetailsHelper
    var toolObject: HATToolsObject? = null
    lateinit var mToolsDetailsHelper: ToolsDetailsHelper
    private var pauseFlag = false
    private lateinit var mUserHelper : UserHelper
    private var profileInfo : List<ProfileInfoDataPreviewObject>? = null
    private var listProf : List<Map<String,Any>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.data_preview_fragment)
        mUserHelper = UserHelper(this)
        if(intent.extras != null){
            dataPlug = intent.extras!!.getSerializable("dataplug") as? HATApplicationObject
            toolObject = intent.extras!!.getSerializable("toolobject") as? HATToolsObject
        }
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        listHeader = inflater.inflate(R.layout.data_plugs_detailed_header, null)
        val gotoHatApp = listHeader.findViewById<TextView>(R.id.goToHatAppDataPreviewBtn)

        if(dataPlug != null){
            mDataPlugsDetailsHelper = DataPlugsDetailsHelper(this, listHeader, 1)
            mDataPlugsDetailsHelper.initValues(dataPlug!!)
//            endPoint = dataPlug?.application?.status?.dataPreviewEndpoint
//            getProfileInfo(dataPlug?.application?.id)
            gotoHatApp?.visibility = View.VISIBLE
        }
        if(toolObject != null){
           mToolsDetailsHelper = ToolsDetailsHelper(this,listHeader,1)
            mToolsDetailsHelper.initValues(toolObject!!)
            endPoint = toolObject?.info?.dataPreviewEndpoint
        }

        listView = findViewById(R.id.dataPreviewListview)
        listView?.addHeaderView(listHeader)
        listView?.adapter = DataPreviewFeedAdapter(this,null,null,null)
        if(savedInstanceState == null){
//            snackbar = Snackbar.make(findViewById(R.id.dataPreviewLayout), "Fetching previews...", Snackbar.LENGTH_LONG)
//            snackbar?.show()
        }
        if(endPoint != null && toolObject != null){
           getDataPreview(endPoint)
        }
        gotoHatApp?.setOnClickListener {
            goToHatApp()
        }
    }
    private fun goToHatApp(){
        val packageName = "com.hubofallthings.hatappandroid.redirect"
        val intent = Intent(packageName)

        if(dataPlug!=null){
            intent.putExtra("APP_NAME_FROM_INTENT", dataPlug?.application?.id)
        }
        intent.putExtra("USER_DOMAIN_FROM_INTENT", mUserHelper.getUserDomain())
        intent.putExtra("REDIRECT_FROM_INTENT", "data_plugs_preview")
        intent.putExtra("APP_NAME_FROM_INTENT", dataPlug?.application?.id)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent)
        } catch (e : ActivityNotFoundException){
            Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()

        }
    }
    private fun getProfileInfo(source : String?){
        val userDomain = mUserHelper.getUserDomain()
        val token = mUserHelper.getToken()
        val url = "https://$userDomain/api/v2.6/data/$source/profile"
        when(source){
            "facebook"->{
                hasStaticInfo = true
                HATNetworkManager().getRequest(url,listOf("take" to "1","orderBy" to "updated_time","ordering" to "descending")
                    ,mapOf("x-auth-token" to token!!)) {r -> completionProfile(r)}
            }
            "spotify"->{
                hasStaticInfo = true
                HATNetworkManager().getRequest(url,listOf("take" to "1","orderBy" to "dateCreated","ordering" to "descending")
                    ,mapOf("x-auth-token" to token!!)) {r -> completionProfile(r)}
            }
            "fitbit"->{
                hasStaticInfo = true
                HATNetworkManager().getRequest(url,listOf("take" to "1","orderBy" to "dateCreated","ordering" to "descending"),
                    mapOf("x-auth-token" to token!!)) {r -> completionProfile(r)}
            }
            "twitter"->{
                hasStaticInfo = true
                val twitterUrl = "https://$userDomain/api/v2.6/data/twitter/tweets"
                HATNetworkManager().getRequest(twitterUrl,listOf("take" to "1","orderBy" to "id","ordering" to "descending")
                    ,mapOf("x-auth-token" to token!!)) {r -> completionProfile(r)}
            }
        }
        if(!hasStaticInfo){
            getDataPreview(endPoint)
        }
    }
    private fun completionProfile(r : ResultType?){
        Log.i("profileinfojson", r?.statusCode.toString())
        if(r?.json!=null){
            profileInfoToObject(r.json!!)
        }
    }
    private fun profileInfoToObject(json : Json){
        ExecuteJsonInfoToObject(json).execute()
    }
    private fun getDataPreview(endpoint : String?) {
        val myPreference = Preference(this)
        var mostRecent : Long? = null
        var params : List<Pair<String,String>>?= null

        if (myPreference.getLoginStatus()) {
            val encryptedToken = myPreference.getToken()
            val userDomain = myPreference.getUserDomain()
            if(dataPlug != null){
                url = "https://$userDomain/api/v2.6/$endpoint"
                if(dataPlug?.mostRecentData!= null){
                    mostRecent = HATDateHelper().convertIsoToUnix(dataPlug?.mostRecentData)- 2629743 * 3
                    params = listOf("since" to mostRecent.toString())
                }
            }
            if(toolObject != null){
                url = "https://$userDomain/api/v2.6$endpoint"
            }
            if (!encryptedToken.isEmpty()) {
                token = mUserHelper.getToken()
                if (token != null) {
                    HATNetworkManager().getRequest(
                        url,
                        params,
                        mapOf("x-auth-token" to token!!)
                    ) { r -> completion(r) }
                }
            }
        }
    }
    private fun completion(r: ResultType?){
        when(r){
            ResultType.IsSuccess -> {
                Log.i("completion success", r.statusCode.toString())
                if(r.json!=null){
                    streamingArray(r.json!!)
                } else {
                    feedPreviewFlag = true
                    //checkForAsyncResults()
                }
            }
            ResultType.HasFailed ->{
                Log.i("completion", "An error occurred" + r.statusCode.toString())
                if (r.statusCode == 401){
                    //goForLogin()
                }
            }
        }
    }
    //Parse json to List of FeedItems
    private fun streamingArray(json : Json) {
        ExecuteJsonToObject(json).execute()
    }
    override fun onPause() {
        super.onPause()
        pauseFlag = true
    }

    override fun onResume() {
        super.onResume()
        if(pauseFlag){
            if(dataPlug != null){
                mDataPlugsDetailsHelper.onResume()
            }
            pauseFlag = false
        }
    }
    internal inner class ExecuteJsonInfoToObject(val json: Json) : AsyncTask<String, Int, String>() {
        override fun doInBackground(vararg params: String): String {
            val mapper = jacksonObjectMapper()
            mapper.configOverride(String::class.java).setterInfo = JsonSetter.Value.forValueNulls(Nulls.FAIL)
            mapper.setDefaultSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY))
            mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            profileInfo = mapper.readValue<List<ProfileInfoDataPreviewObject>>(json.content)
            return "ok"
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            if(profileInfo!=null && profileInfo!!.isNotEmpty()){
                listProf = profileInfo!![0].data
//                val adapter = DataPreviewFeedAdapter(this@DataPreviewActivity, feedList, listProf,dataPlug?.application?.id)
//                listView?.adapter = adapter
            }
            getDataPreview(endPoint)

            staticInfoFlag = true
            //checkForAsyncResults()
        }
    }
    internal inner class ExecuteJsonToObject(val json: Json) : AsyncTask<String, Int, String>() {
        override fun doInBackground(vararg params: String): String {
            val mapper = jacksonObjectMapper()
            mapper.configOverride(String::class.java).setterInfo = JsonSetter.Value.forValueNulls(Nulls.FAIL)
            mapper.setDefaultSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY))
            mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            feedList = mapper.readValue<List<FeedItem>>(json.content)
            return "ok"
        }
        override fun onPreExecute() {
            super.onPreExecute()
        }
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            feedPreviewFlag = true
            if(feedList == null || feedList!!.isEmpty()){
                val noPreviewFound = listHeader.findViewById<TextView>(R.id.no_preview_found)
                noPreviewFound.visibility = View.GONE //todo data preview not visible
                val gotoHatApp = listHeader.findViewById<TextView>(R.id.goToHatAppDataPreviewBtn)
                gotoHatApp.visibility = View.VISIBLE //todo data preview not visible
            }

            if(feedList != null && feedList!!.isNotEmpty()){
                generateFeedList(feedList)
            }else{
                val adapter = DataPreviewFeedAdapter(this@DataPreviewActivity, feedList, listProf?.get(0)?.toMutableMap(),dataPlug?.application?.id)
                listView?.adapter = adapter
            }
            if(snackbar!=null)
            snackbar?.dismiss()
            //checkForAsyncResults()
        }
    }
    private fun checkForAsyncResults(){
        if(hasStaticInfo){
            if(staticInfoFlag && feedPreviewFlag ){
                val adapter = DataPreviewFeedAdapter(this, feedList, listProf?.get(0)?.toMutableMap(),dataPlug?.application?.id)
                listView?.adapter = adapter
            }
        } else {
            if(feedPreviewFlag ){
                val adapter = DataPreviewFeedAdapter(this, feedList, listProf?.get(0)?.toMutableMap(),dataPlug?.application?.id)
                listView?.adapter = adapter
            }
        }
    }
    private fun generateFeedList(feedList :  List<FeedItem>?){
        if(feedList != null ){
            val adapter = DataPreviewFeedAdapter(this, feedList,listProf?.get(0)?.toMutableMap(),dataPlug?.application?.id)
            listView?.adapter = adapter
        }
    }
}
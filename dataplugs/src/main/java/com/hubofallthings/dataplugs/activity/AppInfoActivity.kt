package com.hubofallthings.dataplugs.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.hubofallthings.android.hatApi.objects.extrernalapps.HATApplicationObject
import com.hubofallthings.android.hatApi.objects.extrernalapps.HATExternalAppsIllustrationObject
import com.hubofallthings.android.hatApi.objects.tools.HATToolsObject
import com.hubofallthings.dataplugs.helpers.DataPlugsDetailsHelper
import com.hubofallthings.dataplugs.R
import com.hubofallthings.dataplugs.adapter.InformationAppInfoAdapter
import com.hubofallthings.dataplugs.adapter.ScreenshotsAppInfoAdapter
import com.hubofallthings.dataplugs.helpers.HATDateHelper
import com.hubofallthings.dataplugs.helpers.ToolsDetailsHelper
import kotlinx.android.synthetic.main.info_apps_details_fragment.*



class AppInfoActivity : AppCompatActivity(){
    private var toolObject : HATToolsObject? = null
    private var dataPlug : HATApplicationObject? = null
    private lateinit var mDataPlugsDetailsHelper : DataPlugsDetailsHelper
    private lateinit var mToolsDetailsHelper: ToolsDetailsHelper
    private lateinit var mHATDateHelper : HATDateHelper
    private var pauseFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.info_apps_details_fragment)
        mHATDateHelper = HATDateHelper()
        val recyclerView = findViewById<RecyclerView>(R.id.screenshotsRecyclerView)
        val horizontalLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView?.layoutManager = horizontalLayoutManager
        recyclerView?.isFocusable = false
        val myView = findViewById<View>(R.id.app_info_header)

        if(intent.extras != null){
            toolObject = intent.extras!!.getSerializable("toolobject") as? HATToolsObject
            dataPlug = intent.extras!!.getSerializable("dataplug") as? HATApplicationObject

            if(toolObject != null){
                initTools(recyclerView ,toolObject?.info?.graphics?.screenshots)
                description_txt.text = toolObject?.info?.description?.text?.trim()
                mToolsDetailsHelper = ToolsDetailsHelper(this,myView,0)
                mToolsDetailsHelper.initValues(toolObject!!)
                informationListView?.isFocusable = false
            }
            if(dataPlug!= null){
                val screenshots = dataPlug?.application?.info?.graphics?.screenshots
                if(screenshots!=null && screenshots.isNotEmpty()){
                    initTools(recyclerView ,dataPlug?.application?.info?.graphics?.screenshots)
                }
                mDataPlugsDetailsHelper = DataPlugsDetailsHelper(this, myView, 0)
                setUpInformationDataPlugs()
                informationListView?.isFocusable = false
                mDataPlugsDetailsHelper.initValues(dataPlug!!)
                description_txt.text = dataPlug?.application?.info?.description?.text?.trim()
            }
        }
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
                pauseFlag = false
            }
        }
    }
    private fun initTools(recyclerView : RecyclerView? , graphics : Array<HATExternalAppsIllustrationObject>?){
        //val screenshots = toolObject?.info?.graphics?.screenshots
        if(graphics != null){
            screenshot_layout.visibility = View.VISIBLE
            screenshotsRecyclerView.visibility = View.VISIBLE
            val adapter = ScreenshotsAppInfoAdapter(this,graphics )
            recyclerView?.adapter = adapter
            recyclerView?.setFocusable(false)
            if(toolObject!=null)
            setUpInformationTools()
        }
    }
    private fun setUpInformationDataPlugs(){
        val result = ArrayList<Pair<String,String?>>()
        result.add("Provider" to dataPlug?.application?.developer?.name)
        result.add("Website" to dataPlug?.application?.developer?.url)
        result.add("Country" to dataPlug?.application?.developer?.country)
        result.add("Version" to dataPlug?.application?.info?.version)
        result.add("Last updated" to mHATDateHelper.parseBirthDate(dataPlug?.application?.status?.versionReleaseDate))
        result.add("Terms and conditions" to dataPlug?.application?.info?.termsUrl)
        result.add("Support email" to dataPlug?.application?.info?.supportContact)
        val adapter = InformationAppInfoAdapter(this,result)
        informationListView.adapter = adapter
        informationListView?.setFocusable(false)
    }
    private fun setUpInformationTools(){
        val result = ArrayList<Pair<String,String?>>()
        result.add("Provider" to toolObject?.developer?.name)
        result.add("Website" to toolObject?.developer?.url)
        result.add("Country" to toolObject?.developer?.country)
        result.add("Version" to toolObject?.info?.version)
        result.add("Last updated" to mHATDateHelper.parseBirthDate(toolObject?.info?.versionReleaseDate))
        result.add("Terms and conditions" to toolObject?.info?.termsUrl)
        result.add("Support email" to toolObject?.info?.supportContact)

        val adapter = InformationAppInfoAdapter(this,result)
        informationListView.adapter = adapter
        informationListView?.setFocusable(false)
    }
}
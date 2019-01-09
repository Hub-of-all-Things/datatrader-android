package com.hubofallthings.dataplugs.helpers

import android.app.Activity
import android.content.Intent
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hubofallthings.android.hatApi.HATError
import com.hubofallthings.android.hatApi.objects.tools.HATToolsObject
import com.hubofallthings.dataplugs.R
import com.hubofallthings.dataplugs.activity.AppInfoActivity
import com.hubofallthings.dataplugs.activity.DataPreviewActivity
import com.hubofallthings.dataplugs.services.ToolsServices

class ToolsDetailsHelper(private val activity : Activity, view: View , private val tabPosition : Int){
    private var descriptionValue : String? = null
    private var endpoint : String? = null
    private var toolObject : HATToolsObject? = null
    var status : Boolean = false
    private lateinit var mToolsServices : ToolsServices
    lateinit var snackbar :Snackbar

    private val back_button_dataplugs_details = view.findViewById<ImageView>(R.id.back_button_dataplugs_details)
    private val data_plugs_datails_status = view.findViewById<Button>(R.id.data_plugs_datails_status)
    private val bottom_sheet_button = view.findViewById<ImageView>(R.id.bottom_sheet_button)
    private val data_plugs_details_img = view.findViewById<ImageView>(R.id.data_plugs_details_img)

    private val data_plugs_details_name = view.findViewById<TextView>(R.id.data_plugs_details_name)
    private val rating_txt = view.findViewById<TextView>(R.id.rating_txt)
    private val ratingView = view.findViewById<View>(R.id.ratingView)
    private val dataPlugErrorMsg = view.findViewById<TextView>(R.id.dataPlugErrorMsg)
    private val tabLayout =  view.findViewById<TabLayout>(R.id.dataplugsTabs)
    private val headerImage = view.findViewById<ImageView>(R.id.header_img)

    fun initValues(newToolObject: HATToolsObject){
        mToolsServices = ToolsServices(activity)
        toolObject = newToolObject
        descriptionValue = toolObject?.info?.description?.text
        endpoint = toolObject?.info?.dataPreviewEndpoint
        headerImage.setImageResource(R.drawable.tools_header_background)
        Log.i("endpoint" , endpoint)
        initTabs()
        back_button_dataplugs_details.setOnClickListener{onClick(back_button_dataplugs_details)}
        data_plugs_datails_status.setOnClickListener{onClick(data_plugs_datails_status)}
        bottom_sheet_button.setOnClickListener{onClick(bottom_sheet_button)}
        if(status){
            bottom_sheet_button.visibility = View.INVISIBLE
        }
        addValues(toolObject!!)
    }
    private fun initTabs(){
        tabLayout.addTab(tabLayout?.newTab()!!.setText("Tool info"))
        tabLayout.addTab(tabLayout.newTab().setText("Data preview"))
        val tab = tabLayout.getTabAt(tabPosition)
        tab?.select()
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0->{
                        val intent =  Intent(activity, AppInfoActivity::class.java)
                        intent.putExtra("toolobject",toolObject)
                        activity.startActivity(intent)
                        activity.overridePendingTransition(0, 0)
                        activity.finish()
                    }
                    1->{
                        val intent =  Intent(activity, DataPreviewActivity::class.java)
                        intent.putExtra("toolobject",toolObject)
                        activity.startActivity(intent)
                        activity.overridePendingTransition(0, 0)
                        activity.finish()
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
        }
        )
    }

    fun onClick(v: View?) {
        when(v?.id){
            R.id.back_button_dataplugs_details->{
                activity.finish()
            }
            R.id.bottom_sheet_button->{
                showBottomSheetDialog()
            }
            R.id.data_plugs_datails_status->{
                if(!status){
                    enableTool()
                }
            }
        }
    }

    private fun addValues(toolsDto : HATToolsObject){
        Glide.with(activity).load(toolsDto.info.graphics.logo.normal).apply(RequestOptions.circleCropTransform()).into(data_plugs_details_img)
        data_plugs_details_name.text = toolsDto.info.name
        status = toolsDto.status.enabled
        addStatus(toolsDto.status.enabled)
        rating_txt.visibility = View.GONE
        ratingView.visibility = View.GONE
    }
    private fun enableTool(){
        snackbar = Snackbar.make(activity.window.decorView.rootView, "Enabling", Snackbar.LENGTH_INDEFINITE)
        snackbar.show()
        status = false
        val toolsId = toolObject?.id
        if(toolsId!=null)
        mToolsServices.enableTool(toolsId) {r -> completionEnable(r)}
    }
    private fun disableTool(){
        status = false
        snackbar = Snackbar.make(activity.window.decorView.rootView, "Disabling", Snackbar.LENGTH_INDEFINITE)
        snackbar.show()
        val id = toolObject?.id
        if(id!=null)
        mToolsServices.disableTool(id) {r -> completionDisable(r)}
    }
    private fun completionEnable(r : HATToolsObject?){
        snackbar.dismiss()
        toolObject = r
        addStatus(true)
        status = true
        toolObject?.status?.enabled = true
        val toolsId = toolObject?.id
        if(toolsId!= null) {
            mToolsServices.triggerTool(toolsId) { completionTrigger() }
        }
    }
    private fun completionDisable(tool : HATToolsObject?){
        toolObject = tool
        toolObject?.status?.enabled = false
        addStatus(false)
        status = false
        snackbar.dismiss()
    }
    private fun failCallBack(error : HATError?){
        if(error?.errorCode == 401){

        }
    }
    private fun completionTrigger(){

    }

    private fun addStatus(status : Boolean){
        dataPlugErrorMsg.visibility = View.GONE
        if(status){
            bottom_sheet_button.visibility = View.VISIBLE
            data_plugs_datails_status.background = activity.resources.getDrawable(R.drawable.dataplug_button_active,null)
            data_plugs_datails_status.text = activity.getString(R.string.active).toUpperCase()
            data_plugs_datails_status.setTextColor(ContextCompat.getColor(activity, R.color.colorButtonEnabled))
            data_plugs_datails_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.check_data_plug_icon, 0, 0, 0)
        } else {
            bottom_sheet_button.visibility = View.INVISIBLE
            data_plugs_datails_status.background = activity.resources.getDrawable(R.drawable.dataplug_button_connect,null)
            data_plugs_datails_status.text = activity.getString(R.string.connect)
            data_plugs_datails_status.setTextColor(ContextCompat.getColor(activity, R.color.white_color))
            data_plugs_datails_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.add_circle_dataplug_white, 0, 0, 0)
        }
    }

    private fun showBottomSheetDialog() {
        val view = activity.layoutInflater.inflate(R.layout.bottom_sheet_tools, null)
        val dialog = BottomSheetDialog(activity)
        dialog.setContentView(view)
        val cancelBtn = view.findViewById<Button>(R.id.cancel_btn_tool_bottom_sheet)
        val disable = view.findViewById<Button>(R.id.disable_tool_bottom_sheet)
        if(status){
            disable.visibility = View.VISIBLE
        } else {
            disable.visibility = View.GONE
        }
        disable.setOnClickListener {
            disableTool()
            dialog.dismiss()
        }
        cancelBtn.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }
}
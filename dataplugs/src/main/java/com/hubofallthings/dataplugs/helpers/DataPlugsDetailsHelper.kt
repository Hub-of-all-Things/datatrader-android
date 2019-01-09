package com.hubofallthings.dataplugs.helpers

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.net.Uri
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hubofallthings.android.hatApi.HATError
import com.hubofallthings.android.hatApi.objects.extrernalapps.HATApplicationObject
import com.hubofallthings.dataplugs.services.ApplicationConnectionState
import com.hubofallthings.dataplugs.services.DataPlugsServices
import com.hubofallthings.dataplugs.services.ExternalAppsServices
import com.hubofallthings.dataplugs.R
import com.hubofallthings.dataplugs.activity.AppInfoActivity
import com.hubofallthings.dataplugs.activity.DataPreviewActivity
import com.hubofallthings.dataplugs.services.Preference
import java.io.Serializable

class DataPlugsDetailsHelper(private val activity : Activity , view: View , private val tabPosition : Int){
        private var viewPager: ViewPager? = null
        private var descriptionValue : String? = null
        private var endpoint : String? = null
        lateinit var dataPlug : HATApplicationObject
        lateinit var statusDataplug : ApplicationConnectionState
        private var pauseFlag = false
        private var viewDataPLugHeight = 160
    private val packageName = "com.hubofallthings.notables"
    private val back_button_dataplugs_details = view.findViewById<ImageView>(R.id.back_button_dataplugs_details)
    private val data_plugs_datails_status = view.findViewById<Button>(R.id.data_plugs_datails_status)
    private val bottom_sheet_button = view.findViewById<ImageView>(R.id.bottom_sheet_button)
    private val data_plugs_details_img = view.findViewById<ImageView>(R.id.data_plugs_details_img)
    private val headerImg = view.findViewById<ImageView>(R.id.header_img)

    private val data_plugs_details_name = view.findViewById<TextView>(R.id.data_plugs_details_name)
    private val rating_txt = view.findViewById<TextView>(R.id.rating_txt)
    private val ratingView = view.findViewById<View>(R.id.ratingView)
    private val dataPlugRatingMsg = view.findViewById<TextView>(R.id.dataPlugRatingMsg)
    private val view_dataplugs = view.findViewById<View>(R.id.view_dataplugs)
    private val dataPlugErrorMsg = view.findViewById<TextView>(R.id.dataPlugErrorMsg)
    private val tabLayout =  view.findViewById<TabLayout>(R.id.dataplugsTabs)
    private val dataPlugUpdate = view.findViewById<TextView>(R.id.dataPlugUpdate)

    fun initValues(dataPlugInit : HATApplicationObject) {
        dataPlug = dataPlugInit
        descriptionValue = dataPlug.application?.info?.description?.text
        if(dataPlug.mostRecentData != null){
                endpoint = dataPlug.application?.status?.dataPreviewEndpoint
        }
        initTabs()
        back_button_dataplugs_details.setOnClickListener{onClick(back_button_dataplugs_details)}
        data_plugs_datails_status.setOnClickListener{onClick(data_plugs_datails_status)}
        bottom_sheet_button.setOnClickListener({onClick(bottom_sheet_button)})
        if(dataPlug.application?.kind?.kind == "App"){
            headerImg.setImageResource(R.drawable.background_apps_icon)
            val pm = activity.packageManager
            val isInstalled = isPackageInstalled("com.hubofallthings.notables", pm)
        }
        addValues(dataPlug)
    }
    fun onResume(){
        DataPlugsServices(activity)
            .getApp(dataPlug.application?.id, { app ->completionGetApp(app)}, { error -> failCallBack(error)})
    }
    private fun completionGetApp(app : HATApplicationObject?){
        if(app!=null){
            viewDataPLugHeight = 160
            addValues(app)
            dataPlug = app
        }
    }
    private fun failCallBack(error : HATError){
        if(error.errorCode == 401){

        }
    }
    fun onClick(v: View?) {
            when(v?.id){
                R.id.back_button_dataplugs_details ->{
                    activity.finish()
                }
                R.id.bottom_sheet_button ->{
                    showBottomSheetDialog()
                }
                R.id.data_plugs_datails_status ->{
                    openWebDataPlugs()
                }
            }
    }
    private fun isPackageInstalled(packagename : String , packageManager : PackageManager) : Boolean {
        try {
            packageManager.getPackageInfo(packagename, 0)
            return true
        } catch (e : PackageManager.NameNotFoundException) {
            return false
        }
    }
    private fun initTabs(){
        tabLayout.addTab(tabLayout?.newTab()!!.setText("App info"))
        tabLayout.addTab(tabLayout.newTab().setText("Data preview"))
        val tab = tabLayout.getTabAt(tabPosition)
        tab?.select()
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0->{
                        val intent =  Intent(activity, AppInfoActivity::class.java)
                        intent.putExtra("dataplug",dataPlug)
                        activity.startActivity(intent)
                        activity.overridePendingTransition(0, 0)
                        activity.finish()
                    }
                    1->{
                        val intent =  Intent(activity, DataPreviewActivity::class.java)
                        intent.putExtra("dataplug",dataPlug)
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
    private fun openWebDataPlugs(){
        if(statusDataplug != ApplicationConnectionState.Running) {
            if (dataPlug.application?.id == "notables") {
                startNewAppOrMarket()
                return
            }
            val dataplugsId = dataPlug.application?.id
            val dataPlugsUrl = dataPlug.application?.setup?.url
            if (dataPlugsUrl != null && dataplugsId != null) {
                val url = getDataPlugsUrl(dataplugsId, dataPlugsUrl)
                CustomTabsHelper(activity).startBrowser(url)
            }
        }
    }
    private fun startNewAppOrMarket() {
        var intent = activity.packageManager.getLaunchIntentForPackage(packageName)
        if (intent == null) {
            // Bring user to the market or let them choose an app?
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("market://details?id=$packageName")
        } else {
            val sch = dataPlug.application?.setup?.iosUrl
            val userdomain = UserHelper(activity).getUserDomain()
            val url =
                "https://$userdomain/#/hatlogin?name=notables&redirect=$sch&fallback=hatapp://hatappfailed"
            CustomTabsHelper(activity).startBrowser(url)
            return
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            activity.startActivity(intent)
        } catch (e : ActivityNotFoundException){ }
    }
    private fun addValues(plugsDto : HATApplicationObject){
            val ratingUrl = "https://www.hatcommunity.org/hat-dex-rating"
            if(activity != null)
            Glide.with(activity.applicationContext).load(plugsDto.application?.info?.graphics?.logo?.normal).apply(RequestOptions.circleCropTransform()).into(data_plugs_details_img)
            statusDataplug = ExternalAppsServices().getState(plugsDto)
            data_plugs_details_name.text = plugsDto.application?.info?.name
            addStatus(statusDataplug)
            if(plugsDto.application?.info?.rating != null){
                rating_txt.visibility = View.VISIBLE
                ratingView.visibility = View.VISIBLE
                rating_txt.text = plugsDto.application !!.info?.rating?.score
                dataPlugRatingMsg.visibility = View.VISIBLE
                dataPlugRatingMsg.text = String.format(activity.resources.getString(R.string.app_rating_learn_more), plugsDto?.application?.info?.rating?.score)
                dataPlugRatingMsg.setLinkTextColor(activity.resources.getColor(R.color.colorButtonEnabled))
                val learnMoreClickSpan = object : ClickableSpan() {
                    override fun onClick(view: View) {
                        CustomTabsHelper(activity).startBrowser(ratingUrl)
                    }
                }
                viewDataPLugHeight+=55
                makeLinks(dataPlugRatingMsg,arrayOf("Learn more"), arrayOf(learnMoreClickSpan))
                view_dataplugs.layoutParams.height = dpToPx(viewDataPLugHeight).toInt()
            } else {
                rating_txt.visibility = View.GONE
                ratingView.visibility = View.GONE
            }
        }

    private fun makeLinks(textView: TextView, links: Array<String>, clickableSpans: Array<ClickableSpan>) {
        val spannableString = SpannableString(textView.text)
        for (i in links.indices) {
            val clickableSpan = clickableSpans[i]
            val link = links[i]

            val startIndexOfLink = textView.text.toString().indexOf(link)
            spannableString.setSpan(
                clickableSpan, startIndexOfLink,
                startIndexOfLink + link.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        textView.highlightColor = Color.TRANSPARENT // prevent TextView change background when highlight
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.setText(spannableString, TextView.BufferType.SPANNABLE)
    }
    private fun getDataPlugsUrl(appid : String , appurl : String) : String {
        val userDomain = Preference(activity).getUserDomain()
        return "https://$userDomain/#/hatlogin?name=$appid&redirect=$appurl&fallback=hatapp://hatappfailed"
    }
    private fun addStatus(state : ApplicationConnectionState) {
        dataPlugErrorMsg.visibility = View.GONE
        dataPlugUpdate.visibility = View.GONE
        when (state) {
            ApplicationConnectionState.Update -> applicationUpdate()
            ApplicationConnectionState.Failing -> applicationFailing()
            ApplicationConnectionState.Running -> applicationRunning()
            ApplicationConnectionState.Fetching -> applicationFetching()
            ApplicationConnectionState.Untouched -> applicationUntouched()
        }
    }
    private fun applicationRunning(){
        data_plugs_datails_status.background = activity.resources.getDrawable(R.drawable.dataplug_button_active)
        data_plugs_datails_status.text = "ACTIVE"
        data_plugs_datails_status.setTextColor(activity.resources.getColor(R.color.colorButtonEnabled))
        data_plugs_datails_status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check_data_plug_icon, 0, 0, 0)
    }
    private fun applicationUntouched(){
        data_plugs_datails_status.background = activity.resources.getDrawable(R.drawable.dataplug_button_connect)
        data_plugs_datails_status.text = "CONNECT"
        data_plugs_datails_status.setTextColor(activity.resources.getColor(R.color.white_color))
        data_plugs_datails_status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.add_circle_dataplug_white, 0, 0, 0)
    }
    private fun applicationFailing(){
        data_plugs_datails_status.background = activity.resources.getDrawable(R.drawable.dataplug_button_reconnect)
        data_plugs_datails_status.text = "RECONNECT"
        data_plugs_datails_status.setTextColor(activity.resources.getColor(R.color.white_color))
        data_plugs_datails_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.failing_dataplug_white, 0, 0, 0)
        if(dataPlug.application?.kind?.kind == "App"){
            dataPlugErrorMsg.text = "ERROR: App disconnected. Please tap reconnect to reestablish connection"
        }
        viewDataPLugHeight+=55
        dataPlugErrorMsg.visibility = View.VISIBLE
        view_dataplugs.layoutParams.height = dpToPx(viewDataPLugHeight).toInt()
    }
    private fun applicationFetching(){
        data_plugs_datails_status.text = "FETCHING..."
        data_plugs_datails_status.setTextColor(activity.resources.getColor(R.color.colorButtonEnabled))
        data_plugs_datails_status.background = activity.resources.getDrawable(R.drawable.dataplug_button_fetching)
        data_plugs_datails_status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.fetching_data_plugs, 0, 0, 0)
    }
    private fun applicationUpdate(){
        data_plugs_datails_status.text = "UPDATE"
        viewDataPLugHeight+=55
        view_dataplugs.layoutParams.height = dpToPx(viewDataPLugHeight).toInt()
        data_plugs_datails_status.setTextColor(activity.resources.getColor(R.color.white_color))
        data_plugs_datails_status.background = activity.resources.getDrawable(R.drawable.dataplug_button_update)
        data_plugs_datails_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.failing_dataplug_white, 0, 0, 0)
        dataPlugUpdate.visibility = View.VISIBLE
    }

        private fun showBottomSheetDialog() {
            val view = activity.layoutInflater.inflate(R.layout.bottom_sheet_dataplugs, null)
            val dialog = BottomSheetDialog(activity)
            dialog.setContentView(view)
            val reconfigure = view.findViewById<Button>(R.id.reconfigure_dataplug_bottom_sheet)
            val permissionsDataplugs = view.findViewById<Button>(R.id.show_permissions_bottom_sheet)
            val cancelBtn = view.findViewById<Button>(R.id.cancel_btn_bottom_sheet)

            val disable = view.findViewById<Button>(R.id.disable_dataplug_bottom_sheet)
            if(statusDataplug == ApplicationConnectionState.Untouched){
                disable.visibility = View.GONE
                reconfigure.visibility = View.GONE
            }
            if(dataPlug.application?.kind?.kind == "App"){
                reconfigure.visibility = View.GONE
                disable?.text = "Disable app"
            }
            disable.setOnClickListener {
                val appID = dataPlug.application?.id
                if(appID!=null)
                DataPlugsServices(activity).disableApp(appID) { app, _ ->completionDisable(app)}
                dialog.dismiss()
            }
            permissionsDataplugs.setOnClickListener {
                dialog.dismiss()
//                val intent = Intent(activity, PermissionsDataPlugs::class.java) //todo permissions
//                intent.putExtra("dataplug", dataPlug as Serializable)
//                activity.startActivity(intent)
            }
            reconfigure.setOnClickListener {
                openWebDataPlugs()
                dialog.dismiss()
            }
            cancelBtn.setOnClickListener{
                dialog.dismiss()
            }
            dialog.show()
        }
        private fun completionDisable(app : HATApplicationObject?){
            if(app!=null){
                dataPlug = app
                viewDataPLugHeight = 160
                addValues(app)
            }
        }
        private fun dpToPx(dp: Int): Float {
            return (dp * Resources.getSystem().displayMetrics.density)
        }
}
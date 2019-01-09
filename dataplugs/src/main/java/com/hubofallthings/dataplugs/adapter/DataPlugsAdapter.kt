package com.hubofallthings.dataplugs.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.constraint.ConstraintLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hubofallthings.android.hatApi.objects.extrernalapps.HATApplicationObject
import com.hubofallthings.dataplugs.R
import com.hubofallthings.dataplugs.activity.AppInfoActivity
import com.hubofallthings.dataplugs.helpers.CustomTabsHelper
import com.hubofallthings.dataplugs.helpers.UserHelper
import com.hubofallthings.dataplugs.services.ApplicationConnectionState
import com.hubofallthings.dataplugs.services.ExternalAppsServices
import java.io.Serializable

class DataPlugsAdapter(private var activity: Activity, private var items: List<HATApplicationObject>): BaseAdapter() {
    private class ViewHolder(row: View?) {
        var txtTitle: TextView? = null
        var txtSubtitle: TextView? = null
        var imgDataPlug: ImageView? = null
        var dataPlugsPlus : ImageView? = null
        var dataPlugsAction : LinearLayout? = null
        var dataPlugsLayout : ConstraintLayout? = null
        init {
            this.txtTitle = row?.findViewById<TextView>(R.id.externalAppName)
            this.txtSubtitle = row?.findViewById<TextView>(R.id.externalAppSubtitle)
            this.imgDataPlug = row?.findViewById<ImageView>(R.id.externalAppImg)
            this.dataPlugsPlus = row?.findViewById<ImageView>(R.id.dataPlugsPlus)
            this.dataPlugsAction = row?.findViewById<LinearLayout>(R.id.dataPlugsAction)
            this.dataPlugsLayout = row?.findViewById(R.id.data_plugs_layout)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View?
        val viewHolder: ViewHolder

        if (convertView == null) {
            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.dataplugs_list_row, null)
            viewHolder = ViewHolder(view)
            view?.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val plugsDto = items[position]
        val title = plugsDto.application?.info?.name
        val subtitle = plugsDto.application?.info?.headline
        val status = ExternalAppsServices().getState(plugsDto)
        viewHolder.txtTitle?.text = title
        viewHolder.txtSubtitle?.text = subtitle
        Glide.with(activity).load(plugsDto.application?.info?.graphics?.logo?.normal).apply(RequestOptions.circleCropTransform()).into(viewHolder.imgDataPlug!!)
        Glide.with(activity).load(statusIcon(status)).into(viewHolder.dataPlugsPlus!!)

        viewHolder.dataPlugsLayout?.setOnClickListener {
            val intent = Intent(activity, AppInfoActivity::class.java)
            intent.putExtra("dataplug",items[position] as Serializable)
            activity.startActivity(intent)
        }
        viewHolder.dataPlugsAction?.setOnClickListener {
            val dataPlugsId = items[position].application?.id
            val dataPlugsUrl = items[position].application?.setup?.url
            if(dataPlugsId != null && dataPlugsUrl!= null) {
                val url = getDataPlugsUrl(dataPlugsId, dataPlugsUrl)
                CustomTabsHelper(activity).startBrowser(url)
            }
        }
        return view as View
    }

    private fun statusIcon(state : ApplicationConnectionState) : Int{
        return when (state){
            ApplicationConnectionState.Update -> R.drawable.failing_dataplug_icon
            ApplicationConnectionState.Failing -> R.drawable.failing_dataplug_icon
            ApplicationConnectionState.Running -> R.drawable.check_data_plug_icon
            ApplicationConnectionState.Fetching -> R.drawable.fetching_data_plugs
            ApplicationConnectionState.Untouched -> R.drawable.add_circle_data_plug_icon
        }
    }
    private fun getDataPlugsUrl(appid : String , appurl : String) : String  {
        val userdomain = UserHelper(activity).getUserDomain()
        return "https://$userdomain/#/hatlogin?name=$appid&redirect=$appurl&fallback=hatapp://hatappfailed"
        //Preference(activity).setUrlDataPlug(url)
    }
    override fun getItem(i: Int): HATApplicationObject {
        return items[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }

}

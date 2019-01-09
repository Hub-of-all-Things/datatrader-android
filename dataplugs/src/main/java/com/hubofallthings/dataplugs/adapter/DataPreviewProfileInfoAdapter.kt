package com.hubofallthings.dataplugs.adapter

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.hubofallthings.dataplugs.R



class DataPreviewProfileInfoAdapter(private var activity: Activity, private var items:List<Pair<String,Any?>>?): BaseAdapter() {

    private class ViewHolder(row: View?) {
        var txtHint: TextView? = null
        var txtInformation: TextView? = null
        var informationLayout: LinearLayout? = null

        init {
            this.txtHint = row?.findViewById<TextView>(R.id.informationHint)
            this.txtInformation = row?.findViewById<TextView>(R.id.informationText)
            this.informationLayout = row?.findViewById<LinearLayout>(R.id.information_list_layout)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View?
        val viewHolder: ViewHolder

        if (convertView == null) {
            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.information_app_info_list_row, null)
            viewHolder = ViewHolder(view)
            view?.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val item = items!![position]
        viewHolder.txtInformation?.background = activity.resources.getDrawable(R.color.white_color)
        viewHolder.txtHint?.text = getStringResourceByName(item.first).capitalize()
        viewHolder.txtInformation?.text = item.second?.toString()

        return view as View
    }

    private fun getStringResourceByName(aString: String): String {
        val packageName = activity.packageName
        val resId = activity.resources
            .getIdentifier(aString, "string", packageName)
        return if (resId == 0) {
            aString
        } else {
            activity.getString(resId)
        }
    }
    private fun openCustomTabs(url : String?){
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(activity, Uri.parse(url))
    }
    override fun getItem(i: Int): Any? {
        return items
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getCount(): Int {
        if(items!=null){
            return items!!.size
        } else
            return 0
    }

}
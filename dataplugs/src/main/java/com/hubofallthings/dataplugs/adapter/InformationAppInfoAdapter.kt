package com.hubofallthings.dataplugs.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.hubofallthings.dataplugs.R
import com.hubofallthings.dataplugs.helpers.CustomTabsHelper


class InformationAppInfoAdapter(private var activity: Activity, private var items: ArrayList<Pair<String,String?>>): BaseAdapter() {
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
        viewHolder.txtHint?.text = items[position].first
        when(position){
            0,2,3,4 -> {
                viewHolder.txtInformation?.background = activity.resources.getDrawable(R.color.white_color)
                viewHolder.txtInformation?.text = items[position].second
                viewHolder.informationLayout?.setOnClickListener {
                    //do nothing
                    }
            }
            1,5->{
                viewHolder.txtInformation?.background = activity.resources.getDrawable(R.drawable.exit_app_settings)
                viewHolder.informationLayout?.setOnClickListener { openCustomTabs(items[position].second) }
            }
            6->{
                viewHolder.txtInformation?.background = activity.resources.getDrawable(R.drawable.exit_app_settings)
                viewHolder.informationLayout?.setOnClickListener {sendEmail() }
            }
        }
        return view as View
    }
    fun sendEmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:contact@hatdex.org")
        activity.startActivity(emailIntent)
    }
    private fun openCustomTabs(url : String?){
        CustomTabsHelper(activity).startBrowser(url!!)
    }
    override fun getItem(i: Int): ArrayList<Pair<String,String?>> {
        return items
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }

}
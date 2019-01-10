package com.hubofallthings.dataplugs.adapter

import android.app.Activity
import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.hubofallthings.dataplugs.R

class BundlePermissionsAdapter(private var activity: Activity, private var items: ArrayList<Pair<String,String>>): BaseAdapter() {

    private class ViewHolder(row: View?) {
        var txtTitle: TextView? = null
        var txtSubtitle: TextView? = null

        init {
            this.txtTitle = row?.findViewById<TextView>(R.id.permissions_title)
            this.txtSubtitle = row?.findViewById<TextView>(R.id.permissions_subtitle)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View?
        val viewHolder: ViewHolder

        if (convertView == null) {
            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.permissions_list_row, null)
            viewHolder = ViewHolder(view)
            view?.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        viewHolder.txtTitle?.text = items[position].first
        viewHolder.txtSubtitle?.text = fromHtml(items[position].second)
        return view as View
    }
    @Suppress("DEPRECATION")
    private fun fromHtml(html: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html)
        }
    }
    override fun getItem(i: Int):Pair<String, String>?{
        return items[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getCount(): Int {
        return items!!.toList().size
    }

}
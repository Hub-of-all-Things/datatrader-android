package com.hubofallthings.dataplugs.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.hubofallthings.dataplugs.R

class InsightAdapter(private var activity: Activity, private var items: ArrayList<Pair<String,Pair<String,String?>>>): BaseAdapter() {
    private class ViewHolder(row: View?) {
        var imgInsight: ImageView? = null
        var txtContent: TextView? = null
        var txtBadge: TextView? = null

        init {
            this.txtContent = row?.findViewById<TextView>(R.id.insight_content)
            this.txtBadge = row?.findViewById<TextView>(R.id.insight_badge)
            this.imgInsight = row?.findViewById<ImageView>(R.id.insight_img)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View?
        val viewHolder: ViewHolder

        if (convertView == null) {
            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.insight_list_row, null)
            viewHolder = ViewHolder(view)
            view?.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        val source = items[position].first

        when (source){
            "twitter"-> viewHolder.imgInsight?.setImageResource(R.drawable.twitter_feed_icon)
            "facebook"-> viewHolder.imgInsight?.setImageResource(R.drawable.facebook_icon_feed)
            "spotify"-> viewHolder.imgInsight?.setImageResource(R.drawable.spotify_feed_icon)
            "notables"-> viewHolder.imgInsight?.setImageResource(R.drawable.notables_feed_icon)
            "google"-> viewHolder.imgInsight?.setImageResource(R.drawable.calendar_icon)
            "instagram"-> viewHolder.imgInsight?.setImageResource(R.drawable.instagram_feed_icon)
            "fitbit"-> viewHolder.imgInsight?.setImageResource(R.drawable.fitbit_feed_icon)
            "monzo"-> viewHolder.imgInsight?.setImageResource(R.drawable.monzo_feed_image)
            "sentiment","sentiment-neutral","sentiment-positive","sentiment-negative"-> viewHolder.imgInsight?.setImageResource(R.drawable.sentiment_icon)
        }

        viewHolder.txtContent?.text = items[position].second.first
        viewHolder.txtBadge?.text = items[position].second.second

        return view as View
    }

    override fun getItem(i: Int): Pair<String,Pair<String,String?>> {
        return items[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }

}
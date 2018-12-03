package com.hubofallthings.datatrader.adapter

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.hubofallthings.android.hatApi.objects.dataoffers.DataOfferObject
import com.hubofallthings.datatrader.R
import com.hubofallthings.datatrader.helper.HATDateHelper
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.io.IOException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class BrowseOffersAdapter// data is passed into the constructor
internal constructor(val activity: Activity, private val offers: List<DataOfferObject>):
    RecyclerView.Adapter<BrowseOffersAdapter.ViewHolder>() {
    private val TAG = BrowseOffersAdapter::class.java.simpleName
    private val mInflater: LayoutInflater = LayoutInflater.from(activity)
    private var mClickListener: ItemClickListener? = null

    private val transformationCornerHeader = MultiTransformation(
        CenterCrop(),
        RoundedCornersTransformation(20, 0, RoundedCornersTransformation.CornerType.TOP)
    )

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewId = R.layout.browse_offers_item
        val view = mInflater.inflate(viewId, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the view and textview in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val offer = offers[position]
        holder.browseSubtitle?.text = offer.shortDescription
        holder.browseTitle?.text = offer.title
        holder.expiredBrowseDate?.text = HATDateHelper().tryParseDateOutput(offer.offerExpires,"'Expires 'd MMM yyyy")


        Glide.with(activity).load(offer.imageUrl)
            .apply(RequestOptions.bitmapTransform(transformationCornerHeader).placeholder(R.drawable.browse_image_placeholder))
            .into(holder.browseImagePreview)
    }

    // total number of rows
    override fun getItemCount(): Int {
        return offers.size
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        internal val expiredBrowseDate = itemView.findViewById(R.id.expiredBrowseDate) as? TextView
        internal val browseTitle = itemView.findViewById(R.id.browse_title) as? TextView
        internal val browseSubtitle = itemView.findViewById(R.id.browseSubtitle) as? TextView
        internal val browseImagePreview = itemView.findViewById(R.id.browseImagePreview) as ImageView

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            if (mClickListener != null) mClickListener !!.onItemClick(view, adapterPosition)
        }
    }

    // convenience method for getting data at click position
    fun getItem(id: Int): DataOfferObject {
        return offers[id]
    }

    // allows clicks events to be caught
    fun setClickListener(itemClickListener: ItemClickListener) {
        this.mClickListener = itemClickListener
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
    private fun getDate (date : Number ) : String {
        val dateMil = date.toLong()*1000L
        return getDateFormat(dateMil,"d MMM yyyy 'at' HH:mm")
    }
    private fun getDateLocation (date : Number ) : String {
        val dateMil = date.toLong()*1000L
        return getDateFormat(dateMil,"'on' d MMM yyyy 'at' HH:mm")
    }
    private fun getDateFormat(milliSeconds: Long, dateFormat: String): String {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }
}
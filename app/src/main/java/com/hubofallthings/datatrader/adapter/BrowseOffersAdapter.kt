package com.hubofallthings.datatrader.adapter

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.hubofallthings.android.hatApi.objects.dataoffers.DataOfferObject
import com.hubofallthings.datatrader.R
import com.hubofallthings.datatrader.helper.HATDateHelper
import com.hubofallthings.datatrader.utils.Util
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.text.SimpleDateFormat
import android.support.constraint.ConstraintLayout
import android.widget.ProgressBar
import com.hubofallthings.datatrader.activity.OfferDetailsActivity
import com.hubofallthings.datatrader.manager.DataOfferStatusManager
import java.io.Serializable
import java.util.*

class BrowseOffersAdapter
internal constructor(val activity: Activity, private val offers: List<DataOfferObject>) :
    RecyclerView.Adapter<BrowseOffersAdapter.ViewHolder>() {
    private val TAG = BrowseOffersAdapter::class.java.simpleName
    private val mInflater: LayoutInflater = LayoutInflater.from(activity)
    private var mClickListener: ItemClickListener? = null

    private val transformationCornerHeader = MultiTransformation(
        CenterCrop(),
        RoundedCornersTransformation(Util().convertDpToPixel(5f), 0, RoundedCornersTransformation.CornerType.TOP)
    )
    private val requestOptions = RequestOptions()
        .centerCrop()
        .transforms(transformationCornerHeader)

    private val thumbnail = Glide.with(activity)
        .load(R.drawable.browse_offers_placeholder)
        .apply(requestOptions)

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
        holder.expiredBrowseDate?.text = HATDateHelper().tryParseDateOutput(offer.offerExpires, "'Expires 'd MMM yyyy")
        Glide
            .with(activity)
            .load(offer.imageUrl)
            .apply(requestOptions)
            .thumbnail(thumbnail)
            .into(holder.browseImagePreview)

        footerView(holder, offer)
        holder.browseOfferLayout.setOnClickListener {
            val intent = Intent(activity, OfferDetailsActivity::class.java)
            intent.putExtra("offer", offer as Serializable)
            activity.startActivity(intent)
        }
    }

    // total number of rows
    override fun getItemCount(): Int {
        return offers.size
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    private fun footerView(holder: ViewHolder, offer: DataOfferObject) {
        val state = DataOfferStatusManager.getState(offer)
        when (state) {
            DataOfferStatusManager.Accepted -> {
                DataOfferStatusManager.setupProgressBarOld(
                    offer,
                    holder.offerProgressBar,
                    holder.offerProgressTextView,
                    activity
                )
                holder.myOfferBottomLayout.visibility = View.VISIBLE
                holder.browseOfferBottomLayout.visibility = View.GONE
            }
            DataOfferStatusManager.Completed -> {
                holder.myOfferBottomLayout.visibility = View.VISIBLE
                holder.browseOfferBottomLayout.visibility = View.GONE
                DataOfferStatusManager.setupProgressBarOld(
                    offer,
                    holder.offerProgressBar,
                    holder.offerProgressTextView,
                    activity
                )
            }
            DataOfferStatusManager.Available -> {
                holder.browseOfferBottomLayout.visibility = View.VISIBLE
                holder.myOfferBottomLayout.visibility = View.GONE
            }
        }
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        internal val expiredBrowseDate = itemView.findViewById(R.id.expiredBrowseDate) as? TextView
        internal val browseTitle = itemView.findViewById(R.id.browse_title) as? TextView
        internal val browseSubtitle = itemView.findViewById(R.id.browseSubtitle) as? TextView
        internal val browseImagePreview = itemView.findViewById(R.id.browseImagePreview) as ImageView
        internal val browseOfferLayout = itemView.findViewById(R.id.browseOfferLayout) as ConstraintLayout
        internal val browseOfferBottomLayout = itemView.findViewById(R.id.bottomLayoutAvailable) as ConstraintLayout
        internal val myOfferBottomLayout = itemView.findViewById(R.id.bottomLayoutOfferWithProgress) as ConstraintLayout
        internal val offerProgressBar = itemView.findViewById(R.id.offerProgressBar) as ProgressBar
        internal val offerProgressTextView = itemView.findViewById(R.id.offerProgressTextView) as TextView

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            if (mClickListener != null) mClickListener!!.onItemClick(view, adapterPosition)
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

    private fun getDateFormat(milliSeconds: Long, dateFormat: String): String {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }
}

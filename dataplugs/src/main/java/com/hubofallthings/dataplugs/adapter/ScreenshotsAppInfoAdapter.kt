package com.hubofallthings.dataplugs.adapter


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.hubofallthings.android.hatApi.objects.extrernalapps.HATExternalAppsIllustrationObject
import com.hubofallthings.dataplugs.R

class ScreenshotsAppInfoAdapter// data is passed into the constructor
internal constructor(val context: Context, private val mScreenshots: Array<HATExternalAppsIllustrationObject>) :
    RecyclerView.Adapter<ScreenshotsAppInfoAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater
    private var mClickListener: ItemClickListener? = null

    init {
        this.mInflater = LayoutInflater.from(context)
    }

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.screenshot_preview_list_row, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the view and textview in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(mScreenshots[position].normal).into(holder.myImageView)
    }

    // total number of rows
    override fun getItemCount(): Int {
        return mScreenshots.size
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        internal var myImageView: ImageView = itemView.findViewById(R.id.screenshotImageView)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            if (mClickListener != null) mClickListener !!.onItemClick(view, adapterPosition)
        }
    }
    // convenience method for getting data at click position
    fun getItem(id: Int): HATExternalAppsIllustrationObject {
        return mScreenshots[id]
    }

    // allows clicks events to be caught
    fun setClickListener(itemClickListener: ItemClickListener) {
        this.mClickListener = itemClickListener
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
}

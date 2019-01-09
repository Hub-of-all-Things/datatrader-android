package com.hubofallthings.dataplugs.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.hubofallthings.dataplugs.R
import com.hubofallthings.dataplugs.helpers.HATDateHelper
import com.hubofallthings.dataplugs.helpers.HATNetworkHelper
import com.hubofallthings.dataplugs.helpers.UserHelper
import com.hubofallthings.dataplugs.objects.DataFeedNestedStructureItem
import com.hubofallthings.dataplugs.objects.FeedItem
import com.hubofallthings.dataplugs.objects.Location
import com.hubofallthings.dataplugs.services.Preference
import com.hubofallthings.dataplugs.view.NonScrollListView
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.util.Locale

class DataPreviewFeedAdapter(private var activity: Activity, private var items: List<FeedItem>? , private val profileInfo : MutableMap<String,Any>?,private val appId : String?): BaseAdapter() {

    private val mHATDateHelper = HATDateHelper()
    private val mPreference = Preference(activity)
    private val mUserHelper = UserHelper(activity)
    private val mHATNetworkHelper = HATNetworkHelper(activity)
    private val token = mUserHelper.getToken()
    //Multitranform to make rounded corner on the images
    private val imageViewTransformation =  MultiTransformation(
        CenterCrop(),
        RoundedCornersTransformation(20, 0, RoundedCornersTransformation.CornerType.BOTTOM))
    private val transformationCornerHeader = MultiTransformation(
        CenterCrop(),
        RoundedCornersTransformation(20, 0, RoundedCornersTransformation.CornerType.TOP)
    )

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View?
        val viewHolder: ViewHolder

        if(items == null && profileInfo == null){
            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            return inflater.inflate(R.layout.feed_list_row, parent,false)
        }

        if(position==0 && profileInfo!=null){
            val inflater1 = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view1: View? = inflater1.inflate(R.layout.datapreview_profile_information, parent,false)
            val viewHolder1 = ViewHolder(view1)
            view1?.tag = viewHolder1
            var newMap = profileInfo
            val adapter = when(appId) {
                  "facebook" -> {
                      newMap.remove("friends")
                      val updatedTime = newMap["updated_time"]
                      val newUpdatedTime = mHATDateHelper.tryParseDateOutput(updatedTime.toString() , "dd/MM/yyyy HH:mm")
                      newMap.remove("updated_time")
                      newMap["updated_time"] = newUpdatedTime
                      DataPreviewProfileInfoAdapter(activity,newMap.toList())
                }
                "spotify" -> {
                    newMap.remove("images")
                    newMap.remove("followers")
                    newMap.remove("external_urls")
                    val updatedTime = newMap["dateCreated"]
                    val newUpdatedTime = mHATDateHelper.tryParseDateOutput(updatedTime.toString() , "dd/MM/yyyy HH:mm")
                    newMap.remove("dateCreated")
                    newMap["dateCreated"] = newUpdatedTime
                    DataPreviewProfileInfoAdapter(activity,newMap.toList())
                }
                "twitter" -> {
                    val twitterMap = newMap["user"] as? MutableMap<String, Any>
                    twitterMap?.remove("id_str")
                    twitterMap?.remove("entities")
                    DataPreviewProfileInfoAdapter(activity,twitterMap?.toList())
                }
                "fitbit" -> {
                    newMap.remove("topBadges")
                    newMap = translateFitbitValues(newMap)
                    DataPreviewProfileInfoAdapter(activity,newMap?.toList())
                }
                else -> DataPreviewProfileInfoAdapter(activity,newMap.toList())
            }
            viewHolder1.profileInfoList?.adapter = adapter
            viewHolder1.profileInfoList?.isFocusable = false
            return view1 as View
        }
        val feedDto = items!![position]

        if(feedDto.source == "she"){
            if(feedDto.title?.action != null && feedDto.title.action=="insight") {
                val inflater1 = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val view1: View? = inflater1.inflate(R.layout.insight, parent,false)
                val viewHolder1 = ViewHolder(view1)
                view1?.tag = viewHolder1
                val insightContentBadge = getInsight(feedDto.content?.nestedStructure)
                val adapter = InsightAdapter(activity, insightContentBadge)
                viewHolder1.insightListView?.adapter = adapter
                viewHolder1.insightTitle?.text = feedDto.title.text
                viewHolder1.insightSubtitle?.text = feedDto.title.subtitle

                Glide.with(activity).load(R.drawable.insight_header_icon)
                    .apply(bitmapTransform(transformationCornerHeader))
                    .into(viewHolder1.insightHeader !!)
                return view1 as View
            }
        }
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = if (hasImage(feedDto)){
            inflater.inflate(R.layout.feed_list_row, parent,false)
        } else {
            inflater.inflate(R.layout.feed_row_without_image, parent,false)
        }

        viewHolder = ViewHolder(view)
        view?.tag = viewHolder

        addDateAndTime(position,feedDto,viewHolder)

        viewHolder.imgTitleTxt?.text = ""
        if (feedDto.location != null){
            Glide.with(activity).load(getMapUrl(feedDto.location))
                .apply(bitmapTransform(imageViewTransformation))
                .into(viewHolder.imgFeed!!)
        }

        //loading image from url
        if(feedDto.content?.media?.get(0)?.thumbnail != null){
            ExecuteImage(feedDto.content.media[0].thumbnail,viewHolder.imgFeed!!, token,position).execute()
        }
        viewHolder.txtTitle?.text =  feedDto.title?.text?.trim()//  title
        if(hasImage(feedDto)){
            if(feedDto.source == "google"|| feedDto.source == "monzo"){
                if(!feedDto.title?.subtitle!!.trim().isEmpty()){
                    viewHolder.txtSubtitle?.visibility = View.VISIBLE
                    viewHolder.txtSubtitle?.text = feedDto.title.subtitle.trim()
                }else{
                    viewHolder.txtSubtitle?.visibility = View.GONE
                }
                viewHolder.imgTitleTxt?.text = feedDto.location?.address?.name?.trim()
            }else{
                viewHolder.txtSubtitle?.visibility = View.GONE
                viewHolder.imgTitleTxt?.text = feedDto.content?.text
            }
        } else {
            if(feedDto.source == "google" || feedDto.source == "monzo"){
                if(!feedDto.title?.subtitle!!.trim().isEmpty()){
                    viewHolder.txtSubtitle?.visibility = View.VISIBLE
                    viewHolder.txtSubtitle?.text = feedDto.title.subtitle.trim()
                }else{
                    viewHolder.txtSubtitle?.visibility = View.GONE
                }
            }else{
                viewHolder.txtSubtitle?.text = feedDto.content?.text
            }
        }

        if(!feedDto.source.isEmpty()){
            viewHolder.imgAvatar?.setImageResource(getImageResource(feedDto.source,feedDto.types))
        }

        viewHolder.imgFeed?.setOnClickListener {
            onClickImageView(feedDto)
        }
        return view as View
    }
    private fun getInsight(items : List<Map<String , Array<DataFeedNestedStructureItem>>>?) : ArrayList<Pair<String,Pair<String,String?>>>{
        val insightArrayList : ArrayList<Pair<String,Pair<String,String?>>> = arrayListOf()
        var source = ""
        var content = ""
        var badge : String? = ""
        var hasSentiment = false
        if(items!=null){
            var stringSentimentContent = ""
            var stringSentimentBadge = ""

            val list = items[0].toList()
            for(i in 0..list.size-1){
                source = list[i].first
                content = list[i].second[0].content
                badge = list[i].second[0].badge

                if(source.contains("sentiment")){
                    stringSentimentContent+= content + "\n"
                    stringSentimentBadge+= badge + "\n"
                    hasSentiment = true
                } else {
                    insightArrayList.add(source to (content to badge))
                }
            }
            if(hasSentiment)
                insightArrayList.add("sentiment" to (stringSentimentContent.trim() to stringSentimentBadge.trim()))
        }
        return insightArrayList
    }
    private fun onClickImageView(feedDto : FeedItem){
        val imageUrl = feedDto.content?.media?.get(0)?.url
        if(imageUrl != null) {
            mPreference.setImageUrl(imageUrl)
            startImagePreviewActivity()
        } else if (feedDto.location != null){
            mPreference.setImageUrl(getMapUrl(feedDto.location))
            startImagePreviewActivity()
        }
    }
    private fun startImagePreviewActivity(){
//        val intent = Intent(activity, ImagePreviewActivity::class.java) //todo imagepreview
//        activity.startActivity(intent)
    }
    private fun getMapUrl(location : Location) : String{
        val longitude = location.geo?.longitude
        val latitude = location.geo?.latitude
        return if(location.address?.name!= null) {
            "https://maps.googleapis.com/maps/api/staticmap?center=${location.address.name}&markers=Color.red|${location.address.name}&zoom=16&size=600x450&maptype=normal&key=AIzaSyBFUVQdgAkCMLWeyihT1NGRh25pzMODUsE"
        }else{
            "https://maps.googleapis.com/maps/api/staticmap?center=$latitude,$longitude&markers=Color.red|$latitude,$longitude&zoom=16&size=600x450&maptype=normal&key=AIzaSyBFUVQdgAkCMLWeyihT1NGRh25pzMODUsE"
        }
    }
    private var executeOnce = false
    private fun translateFitbitValues(fitbitMap : MutableMap<String,Any>?):MutableMap<String,Any>?{
            try {
                if(!executeOnce) {
                    val height = fitbitMap?.get("height").toString()
                    val weight = fitbitMap?.get("weight").toString()
                    if (UnitLocale.default == 0) {
                        val newHeight = convertTofeetInches(height.toDouble())
                        val newWeight = convertToLbs(weight.toDouble())
                        if (fitbitMap?.containsKey("height")!!) {
                            fitbitMap.put("height", newHeight)
                        }
                        if (fitbitMap.containsKey("weight")) {
                            fitbitMap.put("weight", newWeight)
                        }
                    } else {
                        if (fitbitMap?.containsKey("height")!!) {
                            fitbitMap.put("height", "$height cm")
                        }
                        if (fitbitMap.containsKey("weight")!!) {
                            fitbitMap.put("weight", "$weight kg")
                        }
                    }
                    executeOnce = true
                }
            }catch (e : NumberFormatException){ }
        return fitbitMap
    }
    private fun convertToLbs(weight : Double) : String{
        val nearExact = weight/0.45359237
        val lbs = Math.floor(nearExact)
        return "$lbs lbs"
    }
    object UnitLocale {
            var Imperial = Int
            var Metric = Int
            val default: Int
                get() = getFrom(Locale.getDefault())

            fun getFrom(locale: Locale): Int {
                val countryCode = locale.country.toUpperCase()
                Log.i("countrycode",countryCode)
                return when(countryCode){
                    "US", "LR", "MM" -> 0
                    else -> 1
                }
            }
        }

    @Throws(NumberFormatException::class)
    private fun convertTofeetInches(value: Double) : String {
        val feet = Math.floor(value / 30.48).toInt()
        val inches = Math.round(value / 2.54 - feet * 12).toInt()
        val output = feet.toString() + " Foot " + inches + "\""
        return output
    }
    private fun getImageResource(source : String, type : Array<String>): Int{
        return when(source){
            "twitter"-> R.drawable.twitter_feed_icon
            "facebook"-> R.drawable.facebook_icon_feed
            "spotify"-> R.drawable.spotify_feed_icon
            "notables"-> R.drawable.notables_feed_icon
            "google"-> R.drawable.calendar_icon
            "instagram"-> R.drawable.instagram_feed_icon
            "fitbit"-> R.drawable.fitbit_feed_icon
            "monzo"-> R.drawable.monzo_feed_image
            "she"->{
                if(type.contains("sentiment")){
                    R.drawable.sentiment_icon
                } else {
                    R.drawable.she_feed_icon
                }
            }
            else ->{
                return R.drawable.hat_logo_small
            }
        }
    }
    private fun hasImage(feedDto : FeedItem) : Boolean{
        return feedDto.location != null || feedDto.content?.media?.get(0)?.thumbnail != null
    }
    private fun addDateAndTime(position: Int,feedDto: FeedItem,viewHolder : ViewHolder){
        if(position==0){
            viewHolder.feedDate?.text = getDate(feedDto.date.unix)
            viewHolder.feedDateLayout?.requestLayout()
            viewHolder.feedDateLayout?.visibility = View.VISIBLE
        } else {
            if(getDate(feedDto.date.unix) == getDate(items!![position-1].date.unix)){
                viewHolder.feedDateLayout?.visibility = View.GONE
            } else {
                viewHolder.feedDate?.text = getDate(feedDto.date.unix)
                viewHolder.feedDateLayout?.visibility = View.VISIBLE
            }
        }
        viewHolder.feedTime?.text = getTime(feedDto.date.unix)
    }

    private fun getDate (date : Number ) : String {
        val dateMil = date.toLong()*1000L
        return mHATDateHelper.getDateFormat(dateMil,"EEE d MMM yyyy")
    }
    private fun getTime (date : Number ) : String {
        val dateMil = date.toLong()*1000L
        return mHATDateHelper.getDateFormat(dateMil,"HH:mm ")
    }

    override fun getItem(i: Int): FeedItem {
        return items!![i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getCount(): Int {
        if(items == null && profileInfo == null){
            return 0
        } else if(profileInfo!=null && profileInfo.isNotEmpty()){
            if(items!=null && items!!.isNotEmpty()){
                return items!!.size
            } else{
                return 1
            }
        } else {
            if(items!=null){
                return items!!.size
            }
        }
        return 0
    }
    private class ViewHolder(row: View?) {
        var txtTitle: TextView? = null
        var txtSubtitle: TextView? = null
        var imgFeed: ImageView? = null
        var imgAvatar: ImageView? = null
        var imageFrameLayout: FrameLayout? = null
        var imgTitleTxt : TextView? = null
        var feedTime : TextView? = null
        var feedDateLayout : LinearLayout? = null
        var feedDate : TextView? = null
        var insightListView : ListView? = null
        var insightTitle : TextView? = null
        var insightSubtitle : TextView? = null
        var insightHeader : ImageView? = null
        var titleLayout : LinearLayout? = null
        var profileInfoList : NonScrollListView?= null

        init {
            this.txtTitle = row?.findViewById(R.id.txtTitle)
            this.txtSubtitle = row?.findViewById(R.id.txtSubtitle)
            this.imgFeed = row?.findViewById(R.id.imgFeed)
            this.imgAvatar = row?.findViewById(R.id.imgAvatar)
            this.imageFrameLayout = row?.findViewById(R.id.imageFrameLayout)
            this.imgTitleTxt = row?.findViewById(R.id.imgTitleTxt)
            this.feedTime = row?.findViewById(R.id.timeFeed)
            this.feedDate = row?.findViewById(R.id.dateTxt)
            this.feedDateLayout = row?.findViewById(R.id.dateFeedLay)
            this.insightListView = row?.findViewById(R.id.insightListView)
            this.insightTitle = row?.findViewById(R.id.insightTitle)
            this.insightSubtitle = row?.findViewById(R.id.insightSubtitle)
            this.insightHeader = row?.findViewById(R.id.insightHeader)
            this.titleLayout = row?.findViewById(R.id.title_layout)
            this.profileInfoList = row?.findViewById(R.id.profile_info_list)
        }
    }
    internal inner class ExecuteImage(val url : String? ,val feedImg : ImageView?, val token : String?,val position: Int) : AsyncTask<String, Int, GlideUrl>() {
        override fun doInBackground(vararg params: String): GlideUrl? {
            return GlideUrl(url, LazyHeaders.Builder()
                .addHeader("x-auth-token", token)
                .build())
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }
        override fun onPostExecute(result: GlideUrl?) {
            super.onPostExecute(result)
            feedImg?.let {
                if(url == items!![position].content?.media?.get(0)?.thumbnail){
                    Glide.with(activity)
                        .load(result)
                        .apply(bitmapTransform(imageViewTransformation))
                        .into(feedImg)
                }else{
                    Glide.with(activity).clear(feedImg)
                    feedImg.setImageDrawable(null)
                }
            }
        }
    }
}
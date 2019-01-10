package com.hubofallthings.dataplugs.activity

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hubofallthings.android.hatApi.objects.dataoffers.DataOfferRequiredDataDefinitionBundleKeyV2
import com.hubofallthings.android.hatApi.objects.extrernalapps.HATApplicationObject
import com.hubofallthings.dataplugs.R
import com.hubofallthings.dataplugs.adapter.BundlePermissionsAdapter
import com.hubofallthings.dataplugs.adapter.PermissionsAdapter
import com.hubofallthings.dataplugs.view.NonScrollListView
import kotlinx.android.synthetic.main.activity_permissions.*


class PermissionsDataPlugs : AppCompatActivity(), View.OnClickListener {
    private var dataPlug : HATApplicationObject? = null
    private var listView: NonScrollListView? = null
    private var listViewBundle: NonScrollListView? = null
    private var listPermissions : ArrayList<Pair<String,String>> = arrayListOf()

    private val ratingUrl = "https://www.hatcommunity.org/hat-dex-rating"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions)

        val intent = intent
        listView = findViewById(R.id.permissions_listview)
        listViewBundle = findViewById(R.id.bundle_listview)

        okayBtnPermissions.setOnClickListener(this)
        dataPlug = intent.getSerializableExtra("dataplug") as HATApplicationObject
        Glide.with(this).load(dataPlug!!.application?.info?.graphics?.logo?.normal).apply(RequestOptions.circleCropTransform()).into(dataplug_permissions_img)
        val res = resources
        val text = String.format(res.getString(R.string.permission_subtitle), dataPlug?.application?.info?.name)
        permissions_sub_txt.text = text
        permissions_title_txt.text = String.format(res.getString(R.string.dataplug_permissions_title), dataPlug?.application?.info?.name)
        if(dataPlug?.application?.info?.rating?.score != null){
            rating_permissions.text = String.format(res.getString(R.string.app_rating_learn_more), dataPlug?.application?.info?.rating?.score)
        } else {
            rating_permissions.text = res.getString(R.string.app_unrated)
        }
        id_permissions.text = dataPlug?.application?.info?.name

        if(dataPlug?.application?.permissions?.rolesGranted != null){
            val adapter = PermissionsAdapter(this, dataPlug?.application?.permissions?.rolesGranted)
            listView?.adapter = adapter
            listView?.isFocusable = false
        }
        if(dataPlug?.application?.permissions?.dataRequired?.bundle?.bundle !=null){
            getBundleTxt(dataPlug?.application?.permissions?.dataRequired?.bundle?.bundle!!)
            dataAccessToLayout.visibility = View.VISIBLE
            val adapter = BundlePermissionsAdapter(this,listPermissions)
            listViewBundle?.adapter = adapter
            listViewBundle?.isFocusable = false
        }
        val rating = findViewById<TextView>(R.id.rating_permissions)
        val learnMoreClickSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this@PermissionsDataPlugs, Uri.parse(ratingUrl))
            }
        }
        if(dataPlug?.application?.info?.rating?.score != null) {
            rating.setLinkTextColor(ContextCompat.getColor(this, R.color.colorButtonEnabled))
            makeLinks(rating, arrayOf("Learn more"), arrayOf(learnMoreClickSpan))
        }
    }

    private fun getBundleTxt(bundleDd : List<Map<String, DataOfferRequiredDataDefinitionBundleKeyV2>>) : ArrayList<Pair<String,String>>{
        for (i in bundleDd.indices){
            bundleDd[i].forEach { (_, value) ->
                if(value.endpoints!=null) {
                    for (j in value.endpoints!!.indices) {
                        var stringToReturn = ""
                        val header = value.endpoints!![j].endpoint
                        //stringToReturn += "<font color='#4a556b'><b>$header</b></font><br />"
                        if (value.endpoints!![j].mapping != null) {
                            value.endpoints!![j].mapping?.forEach { (key, _) ->
                                stringToReturn += "\t\t\t$key<br />"
                            }
                        }
                        listPermissions.add(header to stringToReturn)
                    }
                }
            }
        }
        return listPermissions
    }
    private fun makeLinks(textView: TextView, links: Array<String>, clickableSpans: Array<ClickableSpan>) {
        val spannableString = SpannableString(textView.text)
        for (i in links.indices) {
            val clickableSpan = clickableSpans[i]
            val link = links[i]
            val startIndexOfLink = textView.text.toString().indexOf(link)
            spannableString.setSpan(clickableSpan, startIndexOfLink,
                startIndexOfLink + link.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        textView.highlightColor = Color.TRANSPARENT
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.setText(spannableString, TextView.BufferType.SPANNABLE)
    }
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.okayBtnPermissions->{
             finish()
            }
        }
    }
}
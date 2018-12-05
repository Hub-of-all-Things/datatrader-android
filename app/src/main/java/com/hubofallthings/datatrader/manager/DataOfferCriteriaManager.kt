package com.hubofallthings.datatrader.manager

import android.os.Build
import android.text.Html
import android.text.Spanned
import com.hubofallthings.android.hatApi.objects.dataoffers.DataOfferRequiredDataDefinitionBundleKeyV2

class DataOfferCriteriaManager{
    fun getBundleTxt(bundleDd : List<Map<String, DataOfferRequiredDataDefinitionBundleKeyV2>>) : Spanned{
        var stringToReturn = ""

        for (i in bundleDd.indices){
            bundleDd[i].forEach { (_, value) ->
                if(value.endpoints!=null) {
                    for (j in value.endpoints!!.indices) {
                        val header = value.endpoints!![j].endpoint
                        stringToReturn += "<font color='#4a556b'><b>$header</b></font><br />"
                        if (value.endpoints!![j].mapping != null) {
                            value.endpoints!![j].mapping?.forEach { (key, _) ->
                                stringToReturn += "\t\t\t$key<br />"
                            }
                        }
                    }
                }
            }
        }
        return fromHtml(stringToReturn)
    }
    @Suppress("DEPRECATION")
    private fun fromHtml(html: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html)
        }
    }
}
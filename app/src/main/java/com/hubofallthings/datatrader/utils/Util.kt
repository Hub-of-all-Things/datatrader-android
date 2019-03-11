package com.hubofallthings.datatrader.utils

import android.content.res.Resources

class Util {
    fun convertDpToPixel(dp: Float): Int {
        val metrics = Resources.getSystem().displayMetrics
        val px = dp * (metrics.densityDpi / 160f)
        return Math.round(px)
    }
}
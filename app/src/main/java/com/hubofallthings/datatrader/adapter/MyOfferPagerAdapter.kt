package com.hubofallthings.datatrader.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.hubofallthings.datatrader.R
import com.hubofallthings.datatrader.fragment.OffersAcceptedFragment
import com.hubofallthings.datatrader.fragment.OffersCompletedFragment

class MyOfferPagerAdapter(fm: FragmentManager?, private var mNumOfTabs: Int, private val mContext: Context?, private val numOfAccepted: Int, private val numOfCompleted: Int) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> OffersAcceptedFragment()
            1 -> OffersCompletedFragment()
            else -> null
        }
    }

    override fun getCount(): Int {
        return mNumOfTabs
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> mContext?.getString(R.string.accepted, numOfAccepted.toString())
            1 -> mContext?.getString(R.string.completed, numOfCompleted.toString())
            else -> null
        }
    }
}
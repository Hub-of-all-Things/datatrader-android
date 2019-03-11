package com.hubofallthings.datatrader.fragment

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hubofallthings.datatrader.R
import android.support.v4.view.ViewPager
import android.widget.TextView
import android.widget.Toast
import com.hubofallthings.android.hatApi.HATError
import com.hubofallthings.android.hatApi.objects.dataoffers.DataOfferObject
import com.hubofallthings.android.hatApi.services.HATDataOffersService
import com.hubofallthings.datatrader.adapter.MyOfferPagerAdapter
import com.hubofallthings.datatrader.helper.UserHelper
import com.hubofallthings.datatrader.manager.DataOfferStatusManager

class MyOfferFragment : Fragment(), View.OnClickListener {
    private var tabLayout: TabLayout? = null
    private lateinit var mUserHelper: UserHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.my_offers_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null) {
            mUserHelper = UserHelper(activity!!)
            getAvailableOffers()
            val toolbarTitle: TextView? = activity?.findViewById(R.id.toolbar_title)
            if (toolbarTitle != null) {
                toolbarTitle.text = getString(R.string.my_offers)
            }
        }
    }

    private fun getAvailableOffers() {
        val token = mUserHelper.getToken()
        val userDomain = mUserHelper.getUserDomain()
        val application = "databuyerstaging"
        val merchants = listOf("merchant" to "datatrader")
        Toast.makeText(context, "Fetching offers", Toast.LENGTH_LONG).show()

        if (!token.isNullOrEmpty()) {
            HATDataOffersService().getAvailableDataOffersWithClaims(
                userDomain,
                token,
                application,
                merchants,
                { list, newToken -> successfulCallBack(list, newToken) },
                { error -> failCallBack(error) })
        }
    }

    private fun successfulCallBack(list: List<DataOfferObject>, newToken: String?) {
        val acceptedOffers = ArrayList<DataOfferObject>()
        val completedOffers = ArrayList<DataOfferObject>()

        for (i in list.indices) {
            val state = DataOfferStatusManager.getState(list[i])
            when (state) {
                DataOfferStatusManager.Completed -> {
                    completedOffers.add(list[i])
                }
                DataOfferStatusManager.Accepted -> {
                    acceptedOffers.add(list[i])
                }
                else -> {
                }
            }
        }
        initViewPager(acceptedOffers.size, completedOffers.size)
    }

    private fun failCallBack(error: HATError) {
        Toast.makeText(context, "Error code : ${error.errorCode}", Toast.LENGTH_LONG).show()
    }

    private fun initViewPager(numOfAccepted: Int, numOfCompleted: Int) {
        if (activity != null) {
            tabLayout = activity?.findViewById(R.id.myOfferTabs)

            // Find the view pager that will allow the user to swipe between fragments
            val viewPager = activity?.findViewById(R.id.viewpager) as? ViewPager

            // Create an adapter that knows which fragment should be shown on each page
            val adapter =
                MyOfferPagerAdapter(activity?.supportFragmentManager, 2, context, numOfAccepted, numOfCompleted)
            // Set the adapter onto the view pager
            viewPager?.adapter = adapter

            // Give the TabLayout the ViewPager
            tabLayout?.setupWithViewPager(viewPager)

            // Setting a listener for clicks.
            viewPager?.addOnPageChangeListener(
                TabLayout.TabLayoutOnPageChangeListener(tabLayout)
            )
            tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    viewPager?.currentItem = tab?.position!!
                    when (tab.position) {
                        0 -> {
                        }
                        1 -> {
                        }
                    }
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }
            }
            )
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
        }
    }
}
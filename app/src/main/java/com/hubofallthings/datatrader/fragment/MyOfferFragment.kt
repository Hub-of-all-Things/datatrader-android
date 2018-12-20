package com.hubofallthings.datatrader.fragment

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hubofallthings.datatrader.R
import com.hubofallthings.datatrader.service.BrowseOffersServices
import android.support.v4.view.ViewPager
import com.hubofallthings.datatrader.adapter.MyOfferPagerAdapter


class MyOfferFragment : Fragment() , View.OnClickListener {
    private var tabLayout : TabLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?  {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.my_offers_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(activity!=null){
//            BrowseOffersServices(activity!!).getOffers()
        }
        tabLayout = activity?.findViewById(R.id.myOfferTabs)

        // Find the view pager that will allow the user to swipe between fragments
        val viewPager = activity?.findViewById(R.id.viewpager) as ViewPager

        // Create an adapter that knows which fragment should be shown on each page
        val adapter = MyOfferPagerAdapter( activity!!.supportFragmentManager,2,context)
        // Set the adapter onto the view pager
        viewPager.adapter = adapter

        // Give the TabLayout the ViewPager
        tabLayout?.setupWithViewPager(viewPager)

        // Setting a listener for clicks.
        viewPager.addOnPageChangeListener(
                TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab?.position!!
                when(tab.position){
                    0->{

                    }
                    1->{

                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
        }
        )
//        initTabs()
    }


    override fun onClick(v: View?) {
        when(v?.id){
        }
    }

    private fun initTabs(){
        tabLayout?.addTab(tabLayout?.newTab()!!.setText("Accepted"))
        tabLayout?.addTab(tabLayout?.newTab()!!.setText("Completed"))
//        val tab = tabLayout.getTabAt(tabPosition)
//        tab?.select()
        tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0->{

                    }
                    1->{

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
package com.hubofallthings.datatrader.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hubofallthings.datatrader.R
import com.hubofallthings.datatrader.service.BrowseOffersServices
import com.hubofallthings.datatrader.service.MyOffersAcceptedServices

class OffersAcceptedFragment : Fragment() , View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?  {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.my_offers_accepted_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(activity!=null){
            MyOffersAcceptedServices(activity!!).getOffersAccepted()
        }
    }


    override fun onClick(v: View?) {
        when(v?.id){
        }
    }
}
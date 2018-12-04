package com.hubofallthings.datatrader.service

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.hubofallthings.android.hatApi.HATError
import com.hubofallthings.android.hatApi.objects.dataoffers.DataOfferObject
import com.hubofallthings.android.hatApi.services.HATDataOffersService
import com.hubofallthings.datatrader.R
import com.hubofallthings.datatrader.adapter.BrowseOffersAdapter
import com.hubofallthings.datatrader.helper.UserHelper

class BrowseOffersServices(private val activity : Activity){
    val mUserHelper = UserHelper(activity)
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    fun getOffers(){
        getAvailableOffersWithClaims()
    }

    private fun getAvailableOffersWithClaims(){
        val token = mUserHelper.getToken()
        val userDomain = mUserHelper.getUserDomain()
        val application = "databuyerstaging"

        if(!token.isNullOrEmpty()){
            HATDataOffersService().getAvailableDataOffers(userDomain,token,application,null,{list,newToken->successfulCallBack(list,newToken)},{error->failCallBack(error)})
        }

    }

    private fun successfulCallBack(list: List<DataOfferObject>?,newToken : String?) {
        list?.let {
            viewManager = LinearLayoutManager(activity)
            viewAdapter = BrowseOffersAdapter(activity, it)
            recyclerView = activity.findViewById<RecyclerView>(R.id.browseOffersRecyclerView).apply {
                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                setHasFixedSize(true)

                // use a linear layout manager
                layoutManager = viewManager

                // specify an viewAdapter (see also next example)
                adapter = viewAdapter
            }
        }
        if(!newToken.isNullOrEmpty()){
            mUserHelper.encryptToken(newToken)
        }
    }
    private fun failCallBack(error : HATError){
        error.errorCode
        error.errorMessage
    }
}
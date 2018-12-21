package com.hubofallthings.datatrader.service

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.hubofallthings.android.hatApi.HATError
import com.hubofallthings.android.hatApi.objects.dataoffers.DataOfferObject
import com.hubofallthings.android.hatApi.services.HATDataOffersService
import com.hubofallthings.datatrader.R
import com.hubofallthings.datatrader.activity.MainActivity
import com.hubofallthings.datatrader.adapter.BrowseOffersAdapter
import com.hubofallthings.datatrader.helper.UserHelper
import com.hubofallthings.datatrader.manager.DataOfferStatusManager

class MyOffersAcceptedServices(private val activity : Activity){
    val mUserHelper = UserHelper(activity)
    private val mPreference = DataTraderPreference(activity)
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    fun getOffersAccepted(){
        getAvailableOffers()
    }

    private fun getAvailableOffers(){
        val token = mUserHelper.getToken()
        val userDomain = mUserHelper.getUserDomain()
        val application = "databuyerstaging"
        val merchants = listOf("merchant" to "datatrader")//todo merchant

        if(!token.isNullOrEmpty()){
            HATDataOffersService().getAvailableDataOffersWithClaims(userDomain,token,application,merchants,{ list, newToken->successfulCallBack(list,newToken)},{ error->failCallBack(error)})
        }

    }

    private fun successfulCallBack(list: List<DataOfferObject>?, newToken : String?) {
        list?.let {
            viewManager = LinearLayoutManager(activity)
            val acceptedOffers = ArrayList<DataOfferObject>()

            for(i in list.indices){
                val state = DataOfferStatusManager.getState(list[i])
                if(state == DataOfferStatusManager.Accepted){
                    acceptedOffers.add(list[i])
                }
            }

            viewAdapter = BrowseOffersAdapter(activity, acceptedOffers)
            recyclerView = activity.findViewById<RecyclerView>(R.id.myOffersAcceptedRecyclerView).apply {
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
        if(error.errorCode == 401){
            logout()
        }
    }
    private fun logout(){
        mPreference.setLoginStatus(false)
        val intent = Intent(activity, MainActivity::class.java)
        activity.startActivity(intent)
        activity.finishAffinity()
    }
}
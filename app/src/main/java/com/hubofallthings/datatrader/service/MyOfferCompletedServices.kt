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
import com.hubofallthings.datatrader.adapter.CompletedOffersAdapter
import com.hubofallthings.datatrader.helper.UserHelper
import com.hubofallthings.datatrader.manager.DataOfferStatusManager

class MyOfferCompletedServices(private val activity: Activity) {
    val mUserHelper = UserHelper(activity)
    private val mPreference = DataTraderPreference(activity)
    private var recyclerView: RecyclerView? = null
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    fun getOffersCompleted() {
        getAvailableOffers()
    }

    private fun getAvailableOffers() {
        val token = mUserHelper.getToken()
        val userDomain = mUserHelper.getUserDomain()
        val application = "databuyerstaging"
        val merchants = listOf("merchant" to "datatrader")

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

    private fun successfulCallBack(list: List<DataOfferObject>?, newToken: String?) {
        list?.let {
            viewManager = LinearLayoutManager(activity)
            val acceptedOffers = ArrayList<DataOfferObject>()
            var cash: Int = 0
            var voucher: Int = 0
            var service: Int = 0

            for (i in list.indices) {
                val state = DataOfferStatusManager.getState(list[i])
                if (state == DataOfferStatusManager.Completed) {
                    acceptedOffers.add(list[i])
                    when (list[i].reward.rewardType) {
                        "voucher" -> {
                            voucher++
                        }
                        "cash" -> {
                            cash += list[i].reward.rewardValue.toInt()
                        }
                        "service" -> {
                            service++
                        }
                    }
                }
            }
            val completedHeader = Triple(voucher, cash, service)

            viewAdapter = CompletedOffersAdapter(activity, acceptedOffers, completedHeader)

            recyclerView = activity.findViewById<RecyclerView?>(R.id.myOffersCompletedRecyclerView)
            recyclerView?.let { rv ->
                rv.setHasFixedSize(true)
                rv.layoutManager = viewManager
                rv.adapter = viewAdapter
            }
        }
        if (!newToken.isNullOrEmpty()) {
            mUserHelper.encryptToken(newToken)
        }
    }

    private fun failCallBack(error: HATError) {
        if (error.errorCode == 401) {
            logout()
        }
    }

    private fun logout() {
        mPreference.setLoginStatus(false)
        val intent = Intent(activity, MainActivity::class.java)
        activity.startActivity(intent)
        activity.finishAffinity()
    }
}
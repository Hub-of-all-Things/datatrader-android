package com.hubofallthings.datatrader.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hubofallthings.android.hatApi.HATError
import com.hubofallthings.android.hatApi.managers.HATNetworkManager
import com.hubofallthings.android.hatApi.managers.HATParserManager
import com.hubofallthings.android.hatApi.managers.ResultType
import com.hubofallthings.android.hatApi.objects.dataoffers.DataOfferObject
import com.hubofallthings.android.hatApi.services.HATDataOffersService
import com.hubofallthings.datatrader.R
import com.hubofallthings.datatrader.adapter.BrowseOffersAdapter
import com.hubofallthings.datatrader.helper.UserHelper
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class BrowseOffersFragment : Fragment() , View.OnClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?  {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.browse_offer_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val token = UserHelper(context!!).getToken()
        getAvailableDataOffers(token!!,{list,_-> success(list)},{e -> failCallBack(e)})
    }

    fun success(list: List<DataOfferObject>?) {
        list?.let {
            viewManager = LinearLayoutManager(context)
            viewAdapter = BrowseOffersAdapter(activity!!, it)
            recyclerView = activity!!.findViewById<RecyclerView>(R.id.browseOffersRecyclerView).apply {
                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                setHasFixedSize(true)

                // use a linear layout manager
                layoutManager = viewManager

                // specify an viewAdapter (see also next example)
                adapter = viewAdapter
            }
        }
    }
    fun failCallBack(error : HATError){
        error.errorCode
        error.errorMessage
    }
    override fun onClick(v: View?) {
        when(v?.id){
        }
    }

    fun getAvailableDataOffers(userToken: String, successfulCallBack: (List<DataOfferObject>, String?) -> Unit, failCallBack: (HATError) -> Unit) {
        val url = "https://testing.hubat.net/api/v2.6/applications/databuyerstaging/proxy/api/v2/offersWithClaims"
        val headers = mapOf("x-auth-token" to userToken)
        HATNetworkManager().getRequest(
            url,
            null,
            headers) { r ->
            when (r) {
                ResultType.IsSuccess -> {
                    if (r.statusCode != 401) {
                        val json = r.json!!.content
                        doAsync {
                            val dataOfferObject = HATParserManager().jsonToObjectList(json, DataOfferObject::class.java)
                            uiThread {
                                successfulCallBack(dataOfferObject, r.token)
                            }
                        }
                    }
                }
                ResultType.HasFailed -> {
                    val error = HATError()
                    error.errorCode = r.statusCode
                    error.errorMessage = r.resultString
                    failCallBack(error)
                }
                null -> {
                }
            }
        }
    }
}
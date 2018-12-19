package com.hubofallthings.datatrader.service

import android.app.Activity
import com.hubofallthings.android.hatApi.HATError
import com.hubofallthings.android.hatApi.managers.HATNetworkManager
import com.hubofallthings.android.hatApi.managers.ResultType
import com.hubofallthings.android.hatApi.services.HATDataOffersService
import com.hubofallthings.datatrader.helper.UserHelper
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class OfferDetailsServices(private val activity : Activity){
    private val mUserHelper = UserHelper(activity)

    fun acceptOffer(offerId : String){
        val userDomain = mUserHelper.getUserDomain()
        val token = mUserHelper.getToken()

        if(token!=null){
            claimOffer(userDomain,token,offerId,{s1,s2->successfulCallBack(s1,s2)},{error -> failCallBack(error)})
        }
    }
    fun claimOffer(userDomain: String, userToken: String, offerID: String, successfulCallBack: (String, String?) -> Unit, failCallBack: (HATError) -> Unit) {
        val url = "https://$userDomain/api/v2.6/applications/databuyerstaging/proxy/api/v2/offer/$offerID/claim"
        val headers = mapOf("x-auth-token" to userToken)
        HATNetworkManager().getRequest(
            url,
            null,
            headers) { r ->
            when (r) {
                ResultType.IsSuccess -> {
                    if (r.statusCode != 401) {
                        val json = r.json
                        doAsync {
                            if (json?.array()?.length()!! > 0) {
                                val jObject = json.array().getJSONObject(0)
                                val dataDebitId = jObject.getString("dataDebitId")
                                uiThread {
                                    if (!dataDebitId.isNullOrEmpty()) {
                                        successfulCallBack(dataDebitId, r.token)
                                    }
                                }
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
    private fun successfulCallBack(s1 : String, s2 : String?){

    }
    private fun failCallBack(error : HATError){

    }
}
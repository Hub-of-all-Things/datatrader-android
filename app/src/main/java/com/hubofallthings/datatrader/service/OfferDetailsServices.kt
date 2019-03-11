package com.hubofallthings.datatrader.service

import android.app.Activity
import android.widget.Toast
import com.hubofallthings.android.hatApi.HATError
import com.hubofallthings.android.hatApi.managers.HATNetworkManager
import com.hubofallthings.android.hatApi.managers.ResultType
import com.hubofallthings.android.hatApi.services.HATDataOffersService
import com.hubofallthings.datatrader.helper.ChromeHelper
import com.hubofallthings.datatrader.helper.UserHelper
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class OfferDetailsServices(private val activity: Activity) {
    var urlScheme: String = "datatraderstaging"
    var dataDebitHost: String = "datadebithost"
    private val mUserHelper = UserHelper(activity)

    fun acceptOffer(offerId: String) {
        val userDomain = mUserHelper.getUserDomain()
        val token = mUserHelper.getToken()
        val merchants = listOf("merchant" to "datatrader")
        Toast.makeText(activity, "Accept offer..", Toast.LENGTH_LONG).show()

        if (token != null) {
            claimOffer(userDomain, token, offerId, { _, _ ->
                HATDataOffersService().getAvailableDataOffersWithClaims(
                    userDomain,
                    token,
                    "databuyerstaging",
                    merchants,
                    { dataOffers, _ ->
                        var dataDebitID = ""
                        for (i in dataOffers.indices) {
                            if (offerId == dataOffers[i].dataOfferID) {
                                dataDebitID = dataOffers[i].claim?.dataDebitID ?: ""
                                break
                            }
                        }
//                    val dataDebitID = dataOffers.filter { it.dataOfferID == offerId }.claim?.dataDebitID
                        val url =
                            "https://$userDomain/#/data-debit/$dataDebitID/quick-confirm?redirect=$urlScheme://$dataDebitHost&fallback=$urlScheme/dataDebitFailure"
                        ChromeHelper(activity).startBrowser(url)
                    },
                    { error -> failCallBack(error) })
            }, { error -> failCallBack(error) })
        }
    }

    private fun claimOffer(
        userDomain: String,
        userToken: String,
        offerID: String,
        successfulCallBack: (String, String?) -> Unit,
        failCallBack: (HATError) -> Unit
    ) {
        val url = "https://$userDomain/api/v2.6/applications/databuyerstaging/proxy/api/v2/offer/$offerID/claim"
        val headers = mapOf("x-auth-token" to userToken)
        HATNetworkManager().getRequest(
            url,
            null,
            headers
        ) { r ->
            when (r) {
                ResultType.IsSuccess -> {
                    if (r.statusCode != 401) {
                        val json = r.json
                        val dId = json?.obj()?.getString("dataDebitId")
                        if (!dId.isNullOrEmpty())
                            successfulCallBack(dId, r.token)

                        doAsync {
                            if (json?.array()?.length()!! > 0) {
                                val jObject = json.array().getJSONObject(0)
                                val dataDebitId = jObject.getString("dataDebitId")
                                uiThread {
                                    if (!dataDebitId.isNullOrEmpty()) {
                                        successfulCallBack(dataDebitId, r.token)
                                    }
                                }
                            } else {
                                val error = HATError()
                                error.errorCode = 100
                                error.errorMessage = "no array"
                                uiThread {
                                    failCallBack(error)
                                }
                            }
                        }
                    } else {
                        val error = HATError()
                        error.errorCode = r.statusCode
                        error.errorMessage = r.resultString
                        failCallBack(error)
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

    private fun failCallBack(error: HATError) {
        Toast.makeText(activity, "Error code : ${error.errorCode}", Toast.LENGTH_SHORT).show()
    }
}
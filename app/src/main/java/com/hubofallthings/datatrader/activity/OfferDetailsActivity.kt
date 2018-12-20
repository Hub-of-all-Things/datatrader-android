package com.hubofallthings.datatrader.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hubofallthings.android.hatApi.objects.dataoffers.DataOfferObject
import com.hubofallthings.android.hatApi.services.HATDataOffersService
import com.hubofallthings.datatrader.R
import com.hubofallthings.datatrader.helper.HATDateHelper
import com.hubofallthings.datatrader.manager.DataOfferCriteriaManager
import com.hubofallthings.datatrader.manager.DataOfferStatusManager
import com.hubofallthings.datatrader.service.OfferDetailsServices
import kotlinx.android.synthetic.main.offer_details_activity.*

class OfferDetailsActivity : AppCompatActivity(){
    private var offerObject : DataOfferObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.offer_details_activity)

        if(intent.extras != null) {
            offerObject = intent.extras?.getSerializable("offer") as? DataOfferObject
        }
        initialize()
        acceptOfferBtn.setOnClickListener { acceptOffer() }
    }
    private fun initialize(){
        offerDetailsTitle.text = offerObject?.title
        expiredDateOfferDetails.text = HATDateHelper().tryParseDateOutput(offerObject?.offerExpires,"'Expires 'd MMM yyyy")

        Glide
            .with(this)
            .load(offerObject?.imageUrl)
            .apply(RequestOptions().placeholder(R.drawable.browse_offers_placeholder))
            .into(offerDetailsHeader)

        backButtonOfferDetails.setOnClickListener {finish() }

        offerDetailsDescriptionTitle.text = offerObject?.shortDescription
        offerDetailsDescriptionSubtitle.text = offerObject?.longDescription
        if(offerObject?.dataConditions?.bundle!=null){
            offerDetailsCriteriaTitle.text = DataOfferCriteriaManager().getBundleTxt(offerObject?.dataConditions?.bundle!!)
        }else {
            if (offerObject?.requiredDataDefinition?.bundle != null) {
                offerDetailsCriteriaTitle.text =
                        DataOfferCriteriaManager().getBundleTxt(offerObject?.requiredDataDefinition?.bundle!!)
            }
        }
        checkState()
    }

    private fun checkState(){
        if(offerObject!=null){
            val state = DataOfferStatusManager.getState(offerObject!!)
            when(state){
                DataOfferStatusManager.Accepted -> {
                    offerDetailsProgressBar.visibility = View.VISIBLE
                    offerDetailsProgressTextView.visibility = View.VISIBLE
                    DataOfferStatusManager.setupProgressBarOld(offerObject!!,offerDetailsProgressBar,offerDetailsProgressTextView,this)
                    acceptOfferBtn.visibility = View.GONE
                    offerDetailsActionsTitle.text = getString(R.string.actions_required_string)
                }
                DataOfferStatusManager.Completed -> {
                    offerDetailsProgressBar.visibility = View.VISIBLE
                    offerDetailsProgressTextView.visibility = View.VISIBLE
                    DataOfferStatusManager.setupProgressBarOld(offerObject!!,offerDetailsProgressBar,offerDetailsProgressTextView,this)
                    acceptOfferBtn.visibility = View.GONE
                    offerDetailsActionsTitle.visibility = View.VISIBLE
                    offerDetailsActionsTitle.text = DataOfferStatusManager.setupReward(offerObject!!)
                    offerDetailsActionsRequired.text = "Reward"
                }
                DataOfferStatusManager.Available -> {
                    offerDetailsActionsTitle.text = getString(R.string.actions_required_string)

                }
            }
        }
    }
    private fun acceptOffer(){
        val offerId= offerObject?.dataOfferID
        if(offerId!=null)
        OfferDetailsServices(this).acceptOffer(offerId)
    }
}

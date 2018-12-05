package com.hubofallthings.datatrader.manager

import com.hubofallthings.android.hatApi.objects.dataoffers.DataOfferObject

enum class DataOfferStatusManager{
    Accepted,Completed,Available;

    fun getState(dataOffer: DataOfferObject) : DataOfferStatusManager{
        return when(dataOffer.claim.claimStatus){
            "completed"-> Completed
            "claimed"-> Accepted
            else-> Available
        }
    }
}
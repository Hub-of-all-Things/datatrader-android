package com.hubofallthings.datatrader.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.hubofallthings.datatrader.R
import kotlinx.android.synthetic.main.confirmation_offer.*

class ConfirmationOfferActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.confirmation_offer)

        viewMyOffersBtn.setOnClickListener(this)
        backToBrowseBtn.setOnClickListener(this)
    }
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.viewMyOffersBtn->{}
            R.id.backToBrowseBtn->{finish()}
        }
    }
}
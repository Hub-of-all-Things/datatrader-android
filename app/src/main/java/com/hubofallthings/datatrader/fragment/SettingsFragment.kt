package com.hubofallthings.datatrader.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hubofallthings.datatrader.R
import com.hubofallthings.datatrader.activity.ResetPasswordActivity
import com.hubofallthings.datatrader.service.SettingsServices
import kotlinx.android.synthetic.main.settings_fragment.*

class SettingsFragment : Fragment(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.settings_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logoutSettings.setOnClickListener(this)
        techSupportSettings.setOnClickListener(this)
        termsSettings.setOnClickListener(this)
        goToMyHatApp.setOnClickListener(this)
        resetPasswordSettings.setOnClickListener(this)
        hatEnabledDataSettings.setOnClickListener(this)
        transactionHistory.setOnClickListener(this)
        hatIssuerSettingsTxt.text = SettingsServices().getIssuer()
        vendorSettingsTxt.text = SettingsServices().getVendor()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.logoutSettings -> {
                SettingsServices().logout(activity)
            }
            R.id.techSupportSettings -> {
                SettingsServices().sendEmail(activity)
            }
            R.id.goToMyHatApp -> {
                SettingsServices().startNewAppOrMarket(activity)
            }
            R.id.termsSettings -> {
                Toast.makeText(context, "Not available", Toast.LENGTH_SHORT).show() // todo terms
            }
            R.id.hatEnabledDataSettings -> {
                SettingsServices().openDataPlugs(activity!!)
            }
            R.id.resetPasswordSettings -> {
                val intent = Intent(context, ResetPasswordActivity::class.java)
                activity?.startActivity(intent)
            }
            R.id.transactionHistory -> {
                SettingsServices().openDataDebits(activity)
            }
        }
    }
}
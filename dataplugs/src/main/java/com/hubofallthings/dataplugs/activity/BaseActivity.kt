package com.hubofallthings.dataplugs.activity

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.hubofallthings.dataplugs.receiver.ConnectivityReceiver

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {
    private var mSnackBar: Snackbar? = null
    private val mConnectivityReceiver = ConnectivityReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mConnectivityReceiver)
    }
    private fun showMessage(isConnected: Boolean) {
        if (!isConnected) {
            val messageToUser = "You are offline. Enable your Wifi/cellular data."
            mSnackBar = Snackbar.make(this.window.decorView.rootView, messageToUser, Snackbar.LENGTH_LONG) //Assume "rootLayout" as the root layout of every activity.
            mSnackBar?.show()
        } else {
            mSnackBar?.dismiss()
        }

    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
        registerReceiver(mConnectivityReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }
    /**
     * Callback will be called when there is change
     */
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showMessage(isConnected)
    }
}
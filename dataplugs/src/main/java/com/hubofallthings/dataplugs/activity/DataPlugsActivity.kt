package com.hubofallthings.dataplugs.activity

import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.hubofallthings.dataplugs.R
import com.hubofallthings.dataplugs.helpers.HATNetworkHelper
import com.hubofallthings.dataplugs.helpers.UserHelper
import com.hubofallthings.dataplugs.services.DataPlugsServices
import com.hubofallthings.dataplugs.services.Preference
import kotlinx.android.synthetic.main.dataplugs_activity.*

class DataPlugsActivity : BaseActivity(){
    private var pauseFlag = false
    private var state: Parcelable? = null
    private lateinit var mHATNetworkHelper: HATNetworkHelper
    private lateinit var mUserHelper: UserHelper
    private lateinit var mPreference: Preference

    private lateinit var mDataPlugsServices: DataPlugsServices
    private var snackbar : Snackbar? = null
    private var internetConnection : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dataplugs_activity)
        mDataPlugsServices = DataPlugsServices(this)
        mHATNetworkHelper = HATNetworkHelper(this)
        mUserHelper = UserHelper(this)
        mPreference = Preference(this)
        intent.extras?.let {
            it.getString("userToken")?.let {token->
                mUserHelper.encryptToken(token)
            }
            it.getString("userDomain")?.let { userDomain ->
                mPreference.setUserDomain(userDomain)
            }
            mPreference.setLoginStatus(true)
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        super.onNetworkConnectionChanged(isConnected)
        if(isConnected){
            if(!internetConnection)
                netWorkManager()
        }
    }

    override fun onPause() {
        super.onPause()
        pauseFlag = true
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataPlugsServices.onDestroy()
    }
    override fun onResume() {
        super.onResume()
        if(pauseFlag){
            state = dataPlugListView.onSaveInstanceState()
            netWorkManager()
            pauseFlag = false
        }
    }
    private fun netWorkManager(){
            if(mHATNetworkHelper.isNetworkAvailable()){
                dataPlugs_list.visibility = View.VISIBLE
                mDataPlugsServices.networkManager(state, "DataPlug")
                internetConnection = true
            } else {
                internetConnection = false
//                snackbar = Snackbar.make(activity!!.findViewById(R.id.drawer_layout), "No internet connection", Snackbar.LENGTH_SHORT)
//                if(snackbar != null){
//                    snackbar?.show()
//                }
            }
    }
}
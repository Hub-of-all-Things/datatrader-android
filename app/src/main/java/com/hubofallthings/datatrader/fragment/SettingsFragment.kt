package com.hubofallthings.datatrader.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hubofallthings.datatrader.R
import com.hubofallthings.datatrader.service.SettingsServices
import kotlinx.android.synthetic.main.settings_fragment.*

class SettingsFragment : Fragment() , View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?  {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.settings_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logoutSettings.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.logoutSettings -> {
                SettingsServices().logout(activity)
            }
        }
    }
}
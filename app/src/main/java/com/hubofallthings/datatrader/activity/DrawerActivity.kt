package com.hubofallthings.datatrader.activity

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import com.hubofallthings.datatrader.R
import com.hubofallthings.datatrader.fragment.DrawerFragment

class DrawerActivity : AppCompatActivity(){

    private var toolbar: Toolbar? = null
    private var drawerFragment: DrawerFragment? = null
    private val TAG = DrawerActivity::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)
        Log.i(TAG,"OnCreate..")

        toolbar = findViewById<View>(R.id.toolbar) as Toolbar?

        setSupportActionBar(toolbar)
        getSupportActionBar()!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        drawerFragment = supportFragmentManager.findFragmentById(R.id.fragment_navigation_drawer) as DrawerFragment
        drawerFragment!!.setUpDrawer(R.id.fragment_navigation_drawer, findViewById<View>(R.id.drawer_layout) as DrawerLayout, toolbar!!)

        showOnBoarding()
    }


    private fun showOnBoarding(){
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        // Check if we need to display our OnboardingFragment
        if (!sharedPreferences.getBoolean("onboarding", false)) {
            // The user hasn't seen the OnboardingFragment yet, so show it
            startActivity(Intent(this, OnboardingActivity::class.java))
        }

//        startActivity(Intent(this, OnboardingActivity::class.java))

    }
}
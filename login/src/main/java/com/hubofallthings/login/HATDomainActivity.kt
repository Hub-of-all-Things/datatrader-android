package com.hubofallthings.login

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.hat_domain_activity.*

class HATDomainActivity : AppCompatActivity() , View.OnClickListener  {
    private lateinit var mHatDomainServices : HATDomainServices

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hat_domain_activity)
        mHatDomainServices = HATDomainServices(this)

        mHatDomainServices.checkBuildType()
        back_button_hat_domain.setOnClickListener(this)
        domainOpt1.setOnClickListener(this)
        domainOpt2.setOnClickListener(this)
        domainOpt3.setOnClickListener(this)
        checkDomain()
    }
    private fun checkDomain(){
        when(mHatDomainServices.getDomain()){
            domainOpt1.text->{
                domainOpt1.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.check_domain_icon, 0)
            }
            domainOpt2.text->{
                domainOpt2.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.check_domain_icon, 0)
            }
            domainOpt3.text->{
                domainOpt3.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.check_domain_icon, 0)
            }
        }


    }
    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.domainOpt1 -> {
                mHatDomainServices.setDomain(domainOpt1.text.toString())
            }
            R.id.domainOpt2 -> {
                mHatDomainServices.setDomain(domainOpt2.text.toString())
            }
            R.id.domainOpt3 -> {
                mHatDomainServices.setDomain(domainOpt3.text.toString())
            }
            R.id.back_button_hat_domain->{

            }
        }
        startLoginActivity()
    }

    private fun startLoginActivity(){
        setResult(Activity.RESULT_OK).also {
            finish()
        }
    }
}
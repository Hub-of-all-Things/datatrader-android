package com.hubofallthings.datatrader.activity

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.hubofallthings.datatrader.R
import com.hubofallthings.datatrader.service.DataTraderPreference
import com.hubofallthings.datatrader.service.MainActivityServices
import com.hubofallthings.login.LoginActivity
import com.hubofallthings.signup.activity.CreateAccountActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mMainActivityServices: MainActivityServices
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mMainActivityServices = MainActivityServices(this)
        val myPreference = DataTraderPreference(this)
        loginMainBtn.setOnClickListener(this)
        createAccountMainBtn.setOnClickListener(this)

        val masterKey = mMainActivityServices.getMasterKey()
        if(myPreference.getLoginStatus() && masterKey != null) {
            mMainActivityServices.login()
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.loginMainBtn -> loginBtnClick()
            R.id.createAccountMainBtn-> createAccountBtnClick()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            MainActivityServices.LOGIN->{
                when(resultCode){
                    Activity.RESULT_OK->{
                        data?.let {
                            val extras = data.extras
                            val token = extras?.getString("newToken")
                            val userDomain = extras?.getString("userDomain")
                            mMainActivityServices.success(userDomain,token)
                        }
                    }
                    Activity.RESULT_CANCELED->{

                    }
                }
            }
        }
    }

    private fun loginBtnClick(){
        startActivityForResult(Intent(this, LoginActivity::class.java),MainActivityServices.LOGIN)
    }
    private fun createAccountBtnClick(){
        startActivity(Intent(this, CreateAccountActivity::class.java))
    }
}

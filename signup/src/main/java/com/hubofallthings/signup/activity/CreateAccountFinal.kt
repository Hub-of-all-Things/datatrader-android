package com.hubofallthings.signup.activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.hubofallthings.android.hatApi.HATError
import com.hubofallthings.signup.BuildConfig
import com.hubofallthings.signup.R
import com.hubofallthings.signup.objects.CreateAccountObject
import com.hubofallthings.signup.services.CreateAccountServices
import kotlinx.android.synthetic.main.activity_create_final.*


class CreateAccountFinal : AppCompatActivity(), View.OnClickListener {
    private var executed = false
    lateinit var mCreateObject : CreateAccountObject
    lateinit var mCreateAccountServices : CreateAccountServices

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_final)
        if(intent.extras != null){
            mCreateObject = intent.getSerializableExtra("createobject") as CreateAccountObject
        }
        mCreateAccountServices = CreateAccountServices(this)
        loginToHatBtn.setOnClickListener(this)
        if(savedInstanceState == null){
            getValues()
        }
    }

    private fun getValues(){
        val username =mCreateObject.userName
        val hatNameTv = findViewById<TextView>(R.id.hatNameTxt)
        val hatDomain = findViewById<TextView>(R.id.hatDomainTxt)
        hatNameTv.text = username
        if(BuildConfig.BUILD_TYPE.contentEquals("release")) {
            hatDomain.text = ".hat.direct"
        }else {
            hatDomain.text = ".hubat.net"
        }
        createAccount()
    }

    override fun onClick(p0: View?) {
        startLoginActivity()
    }
    private fun startLoginActivity(){
        mCreateAccountServices.startLoginActivity()
    }
    private fun createAccount(){
        if(!executed){
            executed= true
            mCreateAccountServices.createAccount(mCreateObject, {_,_ -> successFulCallBack()},{e->failCallBack(e)})
        }
    }
    private fun successFulCallBack(){
        Log.i("CreateAccountHAT", "Success")
        createAccountLinearView.visibility = View.VISIBLE
        loginToHatBtn.isEnabled = true
        loginToHatBtn.background = ContextCompat.getDrawable(this, R.drawable.button_enabled_rounded)
        loginToHatBtn.setText(R.string.login_to_hat)
    }
    private fun failCallBack(error : HATError){
        Log.i("createaccount","${error.errorCode}")
        finish()
    }
}
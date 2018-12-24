package com.hubofallthings.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.login_fragment.*

/**
 * Created by Myteletsis Eleftherios on 29/10/2018.
 */
class LoginActivity : AppCompatActivity() ,View.OnClickListener{

    private val loginWeb = 166
    private val domainActivity = 167

    private var hatNameFLag: Boolean = false
    private var hatDomainFlag : Boolean = false
    private var snackbar : Snackbar? = null
    private lateinit var mLoginServices : LoginServices
    private lateinit var mHatDomainServices : HATDomainServices
    private lateinit var mHATNetworkHelper : HATNetworkHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_fragment)

        mHATNetworkHelper = HATNetworkHelper(this)
        mLoginServices = LoginServices(this)
        mHatDomainServices = HATDomainServices(this)
        hatUserDomainLoginEt.setOnClickListener(this)
        hatDomainLoginEt.setOnClickListener(this)
        nextLoginBtn.setOnClickListener(this)
//        back_button_login.setOnClickListener(this)
        setDomain()
        hatUserDomainLoginEt.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                before: Int, count: Int) {
                hatNameFLag = s.isNotEmpty()
                checkValues()
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.nextLoginBtn -> {
                if(isEmpty(hatUserDomainLoginEt)){
                    //Toast.makeText(this,"error", Toast.LENGTH_SHORT).show()
                }else {
                    if(mHATNetworkHelper.isNetworkAvailable()) {
                        nextLoginBtn.isEnabled = false
                        val userDomain = hatUserDomainLoginEt.text.toString().toLowerCase().trim() + hatDomainLoginEt.text.toString().trim()
                        mLoginServices.validateHATPublicKey(userDomain,
                            { _, _ -> successfulCallBack() },
                            { _ -> failCallBack() })
                    } else {
                        snackbar = Snackbar.make(findViewById(R.id.login_layout), "No internet connection", Snackbar.LENGTH_SHORT)
                        if(snackbar != null){
                            snackbar?.show()
                        }
                    }
                }
            }
            R.id.hatDomainLoginEt -> {
                startDomainActivity()
            }
            R.id.back_button_login->{
                setResult(Activity.RESULT_CANCELED).also { finish() }
            }
        }
    }
    fun startDomainActivity(){
        mLoginServices.setHATName(hatUserDomainLoginEt.text.toString())
        val intent = Intent(this, HATDomainActivity::class.java)
        startActivityForResult(intent,domainActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            loginWeb->{
                val token = data?.extras?.getString("newToken")
                setResult(resultCode,data)
                finish()
            }
            domainActivity->{
                initiliazeValues()
            }
        }
    }
    private fun setDomain(){
        hatDomainLoginEt.text = defaultDomain()
        mHatDomainServices.setDomain(defaultDomain())
    }
    private fun successfulCallBack ()  {
        val userDomain = hatUserDomainLoginEt.text.toString().toLowerCase().trim() + hatDomainLoginEt.text.toString().trim()
        mLoginServices.setUserDomain(userDomain)
        mHatDomainServices.setDomain("")
        mLoginServices.setHATName("")
        nextLoginBtn.isEnabled = true

        val intent = Intent(this, WebActivity::class.java)
        startActivityForResult(intent , loginWeb)
    }
    private fun failCallBack ()  {
        val errorMsg = findViewById<LinearLayout>(R.id.errorIncl)
        errorMsg.visibility = View.VISIBLE
        errorMsg.alpha = 0.0f
        errorMsg.animate().alpha(1.0f).duration = 500
        nextLoginBtn.isEnabled = true

        hatUserDomainLoginEt.background = resources.getDrawable(R.drawable.edittext_error , null)
        hatUserDomainLoginEt.setTextColor(ContextCompat.getColor(this, R.color.error_border))

    }
    fun checkValues(){
//        val errorMsg = findViewById<LinearLayout>(R.id.errorIncl)
//        errorMsg.visibility = View.GONE
//        hatUserDomainLoginEt.background = resources.getDrawable(R.color.white_color,null)
//        hatUserDomainLoginEt.setTextColor(ContextCompat.getColor(this, R.color.toolbar_color))

//        if(hatDomainEt.length()>0){
//            hatDomainLogin.text = hatDomainEt.text.trim()
//        }else {
//            hatDomainLogin.text = getString(R.string.your_hat_domain)
//        }
//        if(hatNameEt.length()>0){
//            hatNameLogin.text = hatNameEt.text.trim()
//        }else {
//            hatNameLogin.text= getString(R.string.your_hat_name)
//        }
//        if(hatDomainEt.text.length>4 && hatNameEt.text.trim().length>1){
//            nextLoginBtn.isEnabled = true
//            nextLoginBtn.background = ContextCompat.getDrawable(this, R.drawable.button_enabled_rounded)
//        } else{
//            nextLoginBtn.isEnabled = false
//            nextLoginBtn.background = ContextCompat.getDrawable(this, R.drawable.button_disabled_rounded)
//        }
    }
    private fun defaultDomain() : String{
        return if(BuildConfig.BUILD_TYPE.contentEquals("release")) {
            ".hubofallthings.net"
        }else {
            ".hubat.net"
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED).also { finish() }
    }
    private fun initiliazeValues(){
        val hatDomain = mHatDomainServices.getDomain()
        val hatName = mLoginServices.getHATName()
        hatUserDomainLoginEt.setText(hatName)
        hatDomainLoginEt.setText(hatDomain)
        checkValues()
    }

    private fun isEmpty(etText: EditText): Boolean {
        return etText.text.toString().trim { it <= ' ' }.isEmpty()
    }
}
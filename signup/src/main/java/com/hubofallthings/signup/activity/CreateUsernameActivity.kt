package com.hubofallthings.signup.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.hubofallthings.signup.R
import com.hubofallthings.signup.helpers.HATNetworkHelper
import com.hubofallthings.signup.objects.CreateAccountObject
import com.hubofallthings.signup.services.CreateAccountServices
import kotlinx.android.synthetic.main.activity_create_username.*

class CreateUsernameActivity : AppCompatActivity(), View.OnClickListener {

    var snackbar : Snackbar? = null
    lateinit var mHATNetworkHelper: HATNetworkHelper
    lateinit var mCreateAccountServices: CreateAccountServices
    lateinit var mCreateObject : CreateAccountObject
    var hatUsernameEt : TextInputEditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_username)
        mHATNetworkHelper = HATNetworkHelper(this)
        mCreateAccountServices = CreateAccountServices(this)
        mCreateObject = CreateAccountObject()
        val userName = findViewById<EditText>(R.id.hatUsernameEt)
        val hatDomain = findViewById<TextView>(R.id.hatDomainUsername)
        hatUsernameEt = findViewById(R.id.hatUsernameEt)
        hatDomain.text = getCluster()

        nextCreateUsernameBtn.setOnClickListener(this)
        loginCreateUserName.setOnClickListener(this)
        back_button_create_username.setOnClickListener(this)
        if(intent.extras!=null){
            mCreateObject = intent.getSerializableExtra("createobject") as CreateAccountObject
            if(intent.getBooleanExtra("backbutton",false)){
                userName.setText(mCreateObject.userName)
                nextCreateUsernameBtn.isEnabled = true
                nextCreateUsernameBtn.background = ContextCompat.getDrawable(this, R.drawable.button_enabled_rounded)
            }
        }
        statusBar()

        userName.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
//                if(!s.isEmpty()) {
//                    if (s[0].isLetter()) {
//                    } else {
//                        for(i in 0..s.length -1 ) {
//                            if(s[i].isDigit()){
//                                var temp2 = s.toString().replace(s[i].toString(), "")
//                                if(i+1 <= s.length-1) {
//                                    if (s[i + 1].isLetter()) {
//                                        break
//                                    } else if(s[i + 1].isDigit()){
//                                        temp2 = s.toString().replace(s[i + 1].toString(), "")
//                                    }
//                                }
//                                userName.text = SpannableStringBuilder(temp2)
//                            }
//                        }
//                    }
//                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                    checkValues(s.toString())
            }
        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
        mCreateAccountServices.startCreateAccountActivity(mCreateObject)
    }
    override fun onClick(v: View?) {
        when (v?.id){
            R.id.nextCreateUsernameBtn -> {
                if(mHATNetworkHelper.isNetworkAvailable()){
                    val userNameAddress = hatUsernameEt?.text.toString()
                    if(checkUsernameValues(userNameAddress)){
                        CreateAccountServices(this).validateHAT(userNameAddress , getCluster() , {_ ,_ -> succesfulCallBack()},{error -> failCallBack(error)})
                    } else {
                        snackbar = Snackbar.make(findViewById(R.id.create_account_username_layout), "Username cannot start with a number or have caps", Snackbar.LENGTH_SHORT)
                        if(snackbar != null){
                            snackbar?.show()
                        }
                    }
                }else {
                    snackbar = Snackbar.make(findViewById(R.id.create_account_username_layout), "No internet connection", Snackbar.LENGTH_SHORT)
                    if(snackbar != null){
                        snackbar?.show()
                    }
                }
            }
            R.id.loginCreateUserName -> {
                mCreateAccountServices.startLoginActivity()
            }
            R.id.back_button_create_username->{
                mCreateAccountServices.startCreateAccountActivity(mCreateObject)
            }
        }
    }
    private fun checkUsernameValues(value : String) : Boolean{
        val firstLetter = value[0]
        Log.i("CreateUsernameActivity",firstLetter.toString())
        if(firstLetter.isDigit() || (firstLetter.isLetter() && firstLetter.isUpperCase())){
            Log.i("CreateUsernameActivity","true")
            return false
        }else{
            Log.i("CreateUsernameActivity","false")
            return true
        }
    }
    private fun getCluster(): String{
        return mCreateAccountServices.getCluster()
    }
    private fun succesfulCallBack () {
        mCreateObject.userName = hatUsernameEt?.text?.trim().toString()
        mCreateAccountServices.startPasswordActivity(mCreateObject)
    }
    private fun failCallBack (error: String)  {
        val errorMsg = findViewById<LinearLayout>(R.id.errorIncl)
        val errorTxt = findViewById<TextView>(R.id.errorTxt)
        errorMsg.visibility = View.VISIBLE
        errorMsg.alpha = 0.0f
        errorMsg.animate().alpha(1.0f).duration = 500
        errorTxt.text = error
        hatUsernameEt?.background = resources.getDrawable(R.drawable.edittext_error , null)
        hatUsernameEt?.setTextColor(ContextCompat.getColor(this, R.color.error_border))
    }
    private fun checkValues(text : String) {
        val errorMsg = findViewById<LinearLayout>(R.id.errorIncl)
        errorMsg.visibility = View.GONE
        hatUsernameEt?.background = resources.getDrawable(R.color.white_color,null)
        hatUsernameEt?.setTextColor(ContextCompat.getColor(this, R.color.toolbar_color))

        if(hatUsernameEt!!.length()>0){
            hatNameCreateUsername.text = hatUsernameEt!!.text?.trim()
        }else {
            hatNameCreateUsername.text = resources.getString(R.string.your_name)
        }
        if (text.length > 3) {
            nextCreateUsernameBtn.isEnabled = true
            nextCreateUsernameBtn.background = ContextCompat.getDrawable(this, R.drawable.button_enabled_rounded)
        } else {
            nextCreateUsernameBtn.isEnabled = false
            nextCreateUsernameBtn.background = ContextCompat.getDrawable(this, R.drawable.button_disabled_rounded)
        }
    }
    private fun statusBar(){
        setSupportActionBar(username_toolbar)
        supportActionBar !!.setDisplayShowTitleEnabled(false)
    }
}
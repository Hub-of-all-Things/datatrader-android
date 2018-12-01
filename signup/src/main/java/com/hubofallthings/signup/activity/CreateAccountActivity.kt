package com.hubofallthings.signup.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
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
import kotlinx.android.synthetic.main.activity_create_account.*

class CreateAccountActivity : AppCompatActivity(), View.OnClickListener {
    var firstNameFlag : Boolean = false
    var lastNameFlag : Boolean = false
    var emailFlag : Boolean = false
    var snackbar : Snackbar? = null
    val TAG = CreateAccountActivity::class.java.simpleName
    lateinit var mHATNetworkHelper: HATNetworkHelper
    lateinit var mCreateAccountServices: CreateAccountServices
    lateinit var mCreateObject : CreateAccountObject


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        Log.i(TAG,"On create")

        setSupportActionBar(findViewById(R.id.my_toolbar))
        val firstName = findViewById<EditText>(R.id.firstNameEt)
        val lastName = findViewById<EditText>(R.id.lastNameEt)
        val email = findViewById<EditText>(R.id.emailEt)
        mCreateObject = CreateAccountObject()
        mHATNetworkHelper = HATNetworkHelper(this)
        mCreateAccountServices = CreateAccountServices(this)
        if(intent.extras!= null){
            if(intent.getBooleanExtra("backbutton", false)){
                Log.i(TAG,"BACKBUTTON")
                mCreateObject = intent.getSerializableExtra("createobject") as CreateAccountObject

                firstName.setText(mCreateObject.firstName)
                lastName.setText(mCreateObject.lastName)
                email.setText(mCreateObject.email)

                nextCreateBtn.isEnabled = true
                nextCreateBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_enabled_rounded))
                firstNameFlag = true
                lastNameFlag = true
                emailFlag = true
            }
        }


        supportActionBar!!.setDisplayShowTitleEnabled(false)
        firstName.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if(!isEmpty(s.toString())) {
                    firstNameFlag = true
                    checkValues()
                } else {
                    firstNameFlag = false
                    checkValues()
                }
            }
        })
        lastName.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                    if(!isEmpty(s.toString())){
                        lastNameFlag = true
                        checkValues()
                    } else {
                        lastNameFlag = false
                        checkValues()
                    }

            }
        })
        email.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                        emailFlag = mCreateAccountServices.isEmailValid(email.text.toString())
                        checkValues()
            }
        })
        nextCreateBtn.setOnClickListener(this)
        loginCreateAccount.setOnClickListener(this)
        back_button_create_account.setOnClickListener(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mCreateAccountServices.startMainActivity()
    }
    fun isEmpty(value : String): Boolean {
        return value.trim { it <= ' ' }.isEmpty()
    }
    override fun onClick(v: View?) {
        when (v?.id){
            R.id.nextCreateBtn -> {
                if(mHATNetworkHelper.isNetworkAvailable()){
                    mCreateAccountServices.validateEmailAddress(emailEt.text.toString().trim(),getCluster(), {_, _-> succesfulCallBack() },{error -> failCallBack(error)})
                }else {
                    snackbar = Snackbar.make(findViewById(R.id.create_account_layout), "No internet connection", Snackbar.LENGTH_SHORT)
                    if(snackbar != null){
                        snackbar?.show()
                    }
                }
            }
            R.id.loginCreateAccount -> {
                mCreateAccountServices.startLoginActivity()
            }
            R.id.back_button_create_account->{
                mCreateAccountServices.startMainActivity()
            }
        }
    }
    private fun getCluster(): String{
        return mCreateAccountServices.getCluster()
    }
    private fun succesfulCallBack() {
        mCreateObject.firstName = firstNameEt.text.toString().trim()
        mCreateObject.lastName = lastNameEt.text.toString().trim()
        mCreateObject.email = emailEt.text.toString().trim()

        mCreateAccountServices.startUserNameActivity(mCreateObject,false)
    }
    private fun failCallBack (error: String)  {
        val errorMsg = findViewById<LinearLayout>(R.id.errorIncl)
        val errorTxt = findViewById<TextView>(R.id.errorTxt)
        errorMsg.visibility = View.VISIBLE
        errorMsg.alpha = 0.0f
        errorMsg.animate().alpha(1.0f).duration = 500
        errorTxt.text = error
        emailEt.background = resources.getDrawable(R.drawable.edittext_error , null)
        emailEt.setTextColor(ContextCompat.getColor(this, R.color.error_border))
    }

    fun checkValues(){
        val errorMsg = findViewById<LinearLayout>(R.id.errorIncl)
        errorMsg.visibility = View.GONE
        emailEt.background = resources.getDrawable(R.color.white_color,null)
        emailEt.setTextColor(ContextCompat.getColor(this, R.color.toolbar_color))
        if(firstNameFlag && lastNameFlag && emailFlag){
            nextCreateBtn.isEnabled = true
            nextCreateBtn.background = ContextCompat.getDrawable(this, R.drawable.button_enabled_rounded)
        } else{
            nextCreateBtn.isEnabled = false
            nextCreateBtn.background = ContextCompat.getDrawable(this, R.drawable.button_disabled_rounded)

        }
    }
    }

package com.hubofallthings.signup.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import com.hubofallthings.signup.R
import com.hubofallthings.signup.helpers.HATNetworkHelper
import com.hubofallthings.signup.objects.CreateAccountObject
import com.hubofallthings.signup.services.CreateAccountServices
import com.nulabinc.zxcvbn.Zxcvbn
import kotlinx.android.synthetic.main.activity_create_password.*


class CreatePasswordActivity: AppCompatActivity(), View.OnClickListener {
    var snackbar : Snackbar? = null
    lateinit var mHATNetworkHelper: HATNetworkHelper
    lateinit var mCreateObject : CreateAccountObject
    lateinit var mCreateAccountServices : CreateAccountServices

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_password)
        mHATNetworkHelper = HATNetworkHelper(this)
        mCreateAccountServices = CreateAccountServices(this)

        val password = findViewById<TextInputEditText>(R.id.passwordEt)
        nextCreatePassword.setOnClickListener(this)
        loginCreatePass.setOnClickListener(this)
        back_button_create_password.setOnClickListener(this)

        mCreateObject = CreateAccountObject()
        if(intent.extras != null){
            mCreateObject = intent.getSerializableExtra("createobject") as CreateAccountObject
        }
        statusBar()
        password.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            source.toString().filterNot { it.isWhitespace() }
        })
        password.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                checkValues(s.toString())
            }
        })
    }
    fun checkValues(password : String){
        val zxcvbn = Zxcvbn()
        val strength = zxcvbn.measure(password)
        setPasswordColor(strength.score)
        if(strength.score >= 3){
            nextCreatePassword.isEnabled = true
            nextCreatePassword.background = ContextCompat.getDrawable(this, R.drawable.button_enabled_rounded)
        } else {
            nextCreatePassword.isEnabled = false
            nextCreatePassword.background = ContextCompat.getDrawable(this, R.drawable.button_disabled_rounded)

        }

        when(strength.score){
            0 -> {
                strongScoreTV.setText("")
            }
            1 -> {
                strongScoreTV.setText(R.string.password_strength_poor)
                strongScoreTV.setTextColor(ContextCompat.getColor(this ,R.color.passwordStrengthRed))
            }
            2 -> {

                strongScoreTV.setText(R.string.password_strength_average)
                strongScoreTV.setTextColor(ContextCompat.getColor(this ,R.color.passwordStrengthYellow))
            }
            3 -> {
                strongScoreTV.setText(R.string.password_strength_strong)
                strongScoreTV.setTextColor(ContextCompat.getColor(this , R.color.passwordStrengthGreen))
            }
            4 -> {

                strongScoreTV.setText(R.string.password_strength_very_strong)
                strongScoreTV.setTextColor(ContextCompat.getColor(this ,R.color.passwordStrengthGreen))
            }
        }
    }
    private fun setPasswordColor(strong : Int){
        when (strong){
            0 -> {
                //default color
                passwordProgress1.setBackgroundColor(ContextCompat.getColor(this ,R.color.passwordStrengthDefault))
                passwordProgress2.setBackgroundColor(ContextCompat.getColor(this , R.color.passwordStrengthDefault))
                passwordProgress3.setBackgroundColor(ContextCompat.getColor(this , R.color.passwordStrengthDefault))
                passwordProgress4.setBackgroundColor(ContextCompat.getColor(this ,R.color.passwordStrengthDefault))
            }
            1-> {
                passwordProgress1.setBackgroundColor(ContextCompat.getColor(this ,R.color.passwordStrengthRed))
                passwordProgress2.setBackgroundColor(ContextCompat.getColor(this ,R.color.passwordStrengthDefault))
                passwordProgress3.setBackgroundColor(ContextCompat.getColor(this ,R.color.passwordStrengthDefault))
                passwordProgress4.setBackgroundColor(ContextCompat.getColor(this ,R.color.passwordStrengthDefault))

            }
            2-> {
                passwordProgress1.setBackgroundColor(ContextCompat.getColor(this ,R.color.passwordStrengthYellow))
                passwordProgress2.setBackgroundColor(ContextCompat.getColor(this ,R.color.passwordStrengthYellow))
                passwordProgress3.setBackgroundColor(ContextCompat.getColor(this ,R.color.passwordStrengthDefault))
                passwordProgress4.setBackgroundColor(ContextCompat.getColor(this ,R.color.passwordStrengthDefault))
            }
            3-> {
                passwordProgress1.setBackgroundColor(ContextCompat.getColor(this ,R.color.passwordStrengthGreen))
                passwordProgress2.setBackgroundColor(ContextCompat.getColor(this ,R.color.passwordStrengthGreen))
                passwordProgress3.setBackgroundColor(ContextCompat.getColor(this ,R.color.passwordStrengthGreen))
                passwordProgress4.setBackgroundColor(ContextCompat.getColor(this ,R.color.passwordStrengthDefault))

            }
            4-> {
                passwordProgress1.setBackgroundColor(ContextCompat.getColor(this ,R.color.passwordStrengthGreen))
                passwordProgress2.setBackgroundColor(ContextCompat.getColor(this ,R.color.passwordStrengthGreen))
                passwordProgress3.setBackgroundColor(ContextCompat.getColor(this ,R.color.passwordStrengthGreen))
                passwordProgress4.setBackgroundColor(ContextCompat.getColor(this ,R.color.passwordStrengthGreen))
            }
        }
    }
    private fun statusBar(){
        setSupportActionBar(password_toolbar)
        getSupportActionBar()!!.setDisplayShowTitleEnabled(false)

    }
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.nextCreatePassword -> {
                val password = findViewById<TextInputEditText>(R.id.passwordEt)

                if(mHATNetworkHelper.isNetworkAvailable()){
                    mCreateObject.password = password.text?.trim().toString()

                    mCreateAccountServices.startOptinsActivity(mCreateObject)
                }else {
                    snackbar = Snackbar.make(findViewById(R.id.create_account_password_layout), "No internet connection", Snackbar.LENGTH_SHORT)
                    if(snackbar != null){
                        snackbar?.show()
                    }
                }
            }
            R.id.loginCreatePass -> {
                mCreateAccountServices.startLoginActivity()
            }
            R.id.back_button_create_password->{
                mCreateAccountServices.startUserNameActivity(mCreateObject,true)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        CreateAccountServices(this).startUserNameActivity(mCreateObject,true)
    }
}
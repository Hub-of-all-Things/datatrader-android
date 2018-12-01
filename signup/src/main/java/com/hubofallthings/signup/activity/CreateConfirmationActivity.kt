package com.hubofallthings.signup.activity

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import com.hubofallthings.signup.R
import com.hubofallthings.signup.helpers.HATNetworkHelper
import com.hubofallthings.signup.objects.CreateAccountObject
import com.hubofallthings.signup.services.CreateAccountServices
import kotlinx.android.synthetic.main.activity_create_confirmation.*

class CreateConfirmationActivity : AppCompatActivity(), View.OnClickListener {
    var snackbar : Snackbar? = null
    lateinit var mHATNetworkHelper: HATNetworkHelper
    lateinit var mCreateObject : CreateAccountObject
    lateinit var mCreateAccountServices : CreateAccountServices

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_confirmation)
        val terms = findViewById<View>(R.id.termsTV) as TextView
        mHATNetworkHelper = HATNetworkHelper(this)
        mCreateAccountServices = CreateAccountServices(this)
        if(intent.extras != null){
            mCreateObject = intent.getSerializableExtra("createobject") as CreateAccountObject
        }
        statusBar()
        hatLinksInit(terms)
        confirmBtn.setOnClickListener(this)
        back_button_create_confirmation.setOnClickListener(this)
        terms.setLinkTextColor(Color.WHITE)

    }
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.back_button_create_confirmation->{
                mCreateAccountServices.startOptinsActivity(mCreateObject)
            }
            R.id.confirmBtn->{
                if(mHATNetworkHelper.isNetworkAvailable()){
                    mCreateAccountServices.startCreateFinalActivity(mCreateObject)
                }else {
                    snackbar = Snackbar.make(findViewById(R.id.create_account_confirmation_layout), "No internet connection", Snackbar.LENGTH_SHORT)
                    snackbar?.show()

                }
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        CreateAccountServices(this).startOptinsActivity(mCreateObject)
    }
    fun statusBar(){
        setSupportActionBar(confirmation_toolbar)
        getSupportActionBar()!!.setDisplayShowTitleEnabled(false)

    }
    fun hatLinksInit(terms : TextView){
        val termsClickSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                startMarkDownActivity("Terms of Service")
            }

        }
        val privacyClickSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                startMarkDownActivity("Privacy Policy")

            }

        }
        makeLinks(terms, arrayOf("Terms and Conditions","Privacy Policy"), arrayOf(termsClickSpan,privacyClickSpan))

    }
    fun startMarkDownActivity(link : String){
        CreateAccountServices(this@CreateConfirmationActivity).startMarkDownActivity(this@CreateConfirmationActivity,link)

    }

    fun makeLinks(textView: TextView, links: Array<String>, clickableSpans: Array<ClickableSpan>) {
        val spannableString = SpannableString(textView.text)
        for (i in links.indices) {
            val clickableSpan = clickableSpans[i]
            val link = links[i]

            val startIndexOfLink = textView.text.toString().indexOf(link)
            spannableString.setSpan(clickableSpan, startIndexOfLink,
                    startIndexOfLink + link.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        textView.highlightColor = Color.TRANSPARENT // prevent TextView change background when highlight
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.setText(spannableString, TextView.BufferType.SPANNABLE)
    }
}

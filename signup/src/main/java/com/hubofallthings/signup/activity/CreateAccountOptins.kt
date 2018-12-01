package com.hubofallthings.signup.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hubofallthings.signup.R
import com.hubofallthings.signup.objects.CreateAccountObject
import com.hubofallthings.signup.services.CreateAccountServices
import kotlinx.android.synthetic.main.activity_create_subscribe.*

class CreateAccountOptins : AppCompatActivity(){
    lateinit var mCreateObject : CreateAccountObject
    lateinit var mCreateAccountServices: CreateAccountServices

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_subscribe)
        mCreateObject = CreateAccountObject()
        mCreateAccountServices = CreateAccountServices(this)
        if(intent.extras != null){
            mCreateObject = intent.getSerializableExtra("createobject") as CreateAccountObject
            if(mCreateObject.optins!=null){
                if(mCreateObject.optins?.contains("optinMadHATTERS")!!){
                    optinMadHATTERSBtn.isChecked = true
                }
                if(mCreateObject.optins?.contains("optinHatMonthly")!!){
                    optinHatMonthlyBtn.isChecked = true
                }
                if(mCreateObject.optins?.contains("optinHCF")!!){
                    optinHCFBtn.isChecked = true
                }
            }
        }
        back_button_create_optins.setOnClickListener{
            mCreateAccountServices.startPasswordActivity(mCreateObject)
        }
        nextBtnSubscribe.setOnClickListener {
            val optArray = arrayListOf<String>()
            if(optinMadHATTERSBtn.isChecked){
                optArray.add("optinMadHATTERS")
            }
            if(optinHatMonthlyBtn.isChecked){
                optArray.add("optinHatMonthly")
            }
            if(optinHCFBtn.isChecked){
                optArray.add("optinHCF")
            }

            mCreateObject.optins = optArray
            mCreateAccountServices.startConfirmationActivity(mCreateObject)
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        mCreateAccountServices.startPasswordActivity(mCreateObject)
    }
}
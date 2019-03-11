package com.hubofallthings.datatrader.activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import com.hubofallthings.datatrader.R
import com.hubofallthings.datatrader.fragment.ResetPasswordDialog
import com.hubofallthings.datatrader.service.DataTraderPreference
import kotlinx.android.synthetic.main.password_reset_activity.*

class ResetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.password_reset_activity)
        nextResetPasswordBtn.setOnClickListener { resetPassword(emailResetPassword.text.toString()) }
        back_button_reset_passowrd.setOnClickListener { finish() }

        setupResetHeader()
        emailResetPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (checkValues(s.toString())) {
                    nextResetPasswordBtn.isEnabled = true
                    nextResetPasswordBtn.background =
                        ContextCompat.getDrawable(applicationContext, R.drawable.button_enabled_rounded)
                } else {
                    nextResetPasswordBtn.isEnabled = false
                    nextResetPasswordBtn.background =
                        ContextCompat.getDrawable(applicationContext, R.drawable.button_disabled_rounded)
                }
            }
        })
    }

    private fun setupResetHeader() {
        val userDomain = DataTraderPreference(this).getUserDomain()
        val spl = "\\."
        val user = userDomain.split(spl.toRegex(), 2)
        hatNameResetPassword.text = user[0]
        hatDomainResetPassword.text = user[1]
    }

    private fun resetPassword(email: String) {
        if (checkValues(email)) {
            val dialog = ResetPasswordDialog()
            try {
                dialog.show(this.fragmentManager, email)
            } catch (e: IllegalStateException) {
            }
        }
    }

    private fun checkValues(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
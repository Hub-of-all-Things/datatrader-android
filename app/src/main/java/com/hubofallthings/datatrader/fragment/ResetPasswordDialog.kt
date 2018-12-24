package com.hubofallthings.datatrader.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import com.hubofallthings.datatrader.R
import com.hubofallthings.datatrader.service.HATPasswordServices

class ResetPasswordDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val layoutInflater = LayoutInflater.from(activity)
        val promptView = layoutInflater.inflate(R.layout.reset_password_dialog, null)
        val builder = AlertDialog.Builder(activity)
        val acceptButton = promptView.findViewById<Button>(R.id.dialog_accept_button)

        acceptButton.setOnClickListener {
            //passwordS
            val email = tag
            Log.i("dialogMail",email)
            dismiss()
            if(activity!=null)
                HATPasswordServices(activity).resetPasswordManager(email)
        }
        builder.setView(promptView)
        return builder.create()
    }
}
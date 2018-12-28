package com.hubofallthings.datatrader.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import com.hubofallthings.datatrader.R
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast


class RewardCopyClipboardDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val layoutInflater = LayoutInflater.from(activity)
        val promptView = layoutInflater.inflate(R.layout.reward_copy_fragment, null)
        val builder = AlertDialog.Builder(activity)
        val acceptButton = promptView.findViewById<Button>(R.id.dialog_accept_button)
        val rewardTxt = promptView.findViewById<TextView>(R.id.rewardDescriptionDialog)
        val cancelButton = promptView.findViewById<TextView>(R.id.cancelDialog)

        val voucher = tag
        rewardTxt.text = String.format(getString(R.string.your_voucher_is),voucher)


        acceptButton.setOnClickListener {
            copyText(voucher)
            Toast.makeText(activity,"Copied",Toast.LENGTH_SHORT).show()
            dismiss()
        }
        cancelButton.setOnClickListener {
            dismiss()
        }
        builder.setView(promptView)
        return builder.create()
    }
    fun copyText (value : String){
        val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText("reward", value)
        clipboard!!.primaryClip = clip
    }
}
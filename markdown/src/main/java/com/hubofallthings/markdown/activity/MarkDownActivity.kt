package com.hubofallthings.markdown.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import br.tiagohm.markdownview.MarkdownView
import com.hubofallthings.markdown.R
import kotlinx.android.synthetic.main.activity_markdown.*

class MarkDownActivity : AppCompatActivity() , View.OnClickListener{
    private val termsAndConditions = "https://raw.githubusercontent.com/Hub-of-all-Things/exchange-assets/master/legal/hat-terms-of-service.md"
    private val  privacyPolicy = "https://raw.githubusercontent.com/Hub-of-all-Things/exchange-assets/master/legal/hat-privacy-policy.md"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_markdown)
        val mMarkdownView = findViewById<MarkdownView>(R.id.markdown_view) as MarkdownView
        back_button_markdown.setOnClickListener(this)
        val link = getLink()
        mMarkdownView.loadMarkdownFromUrl(link)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.back_button_markdown->{
                finish()
            }
        }
    }

    private fun getLink() : String {
        val toolbarTitle = findViewById<TextView>(R.id.toolbar_title_markdown)
        val intent = intent
        val link = intent.getStringExtra("link")
        if (link == "Terms of Service"){
            toolbarTitle.text = link
            return termsAndConditions
        } else if (link == "Privacy Policy"){
            toolbarTitle.text = link
            return privacyPolicy
        } else {
            toolbarTitle.text = "Privacy Policy"
            return privacyPolicy
        }
    }

}
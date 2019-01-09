package com.hubofallthings.dataplugs.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView
import android.webkit.WebViewClient
import com.hubofallthings.dataplugs.R
import com.hubofallthings.dataplugs.services.Preference
import kotlinx.android.synthetic.main.activity_web_new.*

class WebviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_new)

        val myPreference = Preference(this)
        val mWebView = findViewById<WebView>(R.id.webView)
        val extras = intent.extras
        val url = extras.get(EXTRA_URL) as? String

        val webSettings = mWebView.settings
        webSettings.setJavaScriptEnabled(true)
        val userDomain = myPreference.getUserDomain()

        mWebView.clearCache(true)

        close_webview.setOnClickListener { finish() }
        refreshWebview.setOnClickListener { mWebView.reload() }

        mWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                // here you can assign parameter "url" to some field in class
                // return true if you want to block redirection, false otherwise
                return false
            }
        }
        mWebView.loadUrl(url)
    }
    companion object {
        var EXTRA_URL = "extra.url"

    }
}
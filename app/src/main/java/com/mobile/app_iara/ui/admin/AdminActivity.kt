package com.mobile.app_iara.ui.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.mobile.app_iara.R
import com.mobile.app_iara.databinding.ActivityAdminBinding
import com.mobile.app_iara.databinding.ActivitySpreadSheetsWebBinding

class AdminActivity : AppCompatActivity() {
    private lateinit var webView: WebView

    private lateinit var binding: ActivityAdminBinding

    companion object {
        const val EXTRA_URL = "EXTRA_URL"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = intent.getStringExtra(EXTRA_URL)

        setupWebView()

        setupBackButton()

        if (url != null) {
            binding.webView.loadUrl(url)
        } else {
            finish()
        }
    }

    private fun setupWebView() {
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = WebViewClient()
    }

    private fun setupBackButton() {
        binding.included.imgBack.setOnClickListener {
            if (binding.webView.canGoBack()) {
                binding.webView.goBack()
            } else {
                finish()
            }
        }
    }
}
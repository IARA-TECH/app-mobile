package com.mobile.app_iara.ui.home.spreadsheets

import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.mobile.app_iara.databinding.ActivitySpreadSheetsWebBinding

class SpreadSheetsWebActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySpreadSheetsWebBinding

    companion object {
        const val EXTRA_URL = "EXTRA_URL"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpreadSheetsWebBinding.inflate(layoutInflater)
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
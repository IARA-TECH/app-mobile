package com.mobile.app_iara.ui.error

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mobile.app_iara.R
import com.mobile.app_iara.ui.start.LoginActivity
import com.mobile.app_iara.utils.NetworkUtils

class WifiErrorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_wifi_error)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tryAgainButton: Button = findViewById(R.id.btErroWifi)

        tryAgainButton.setOnClickListener {
            if (NetworkUtils.isInternetAvailable(this)) {
                // Volta pro Login, que depende do Firebase
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
}

package com.mobile.app_iara.ui.inicio

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.mobile.app_iara.MainActivity
import com.mobile.app_iara.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val splashGif = findViewById<ImageView>(R.id.splashGif)

        val requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .skipMemoryCache(false)
            .dontTransform()

        Glide.with(this)
            .asGif()
            .load(R.drawable.iara_splash)
            .apply(requestOptions)
            .transition(DrawableTransitionOptions.withCrossFade(200))
            .into(splashGif)

        lifecycleScope.launch {
            delay(6250)

            val sharedPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val isLoggedIn = sharedPrefs.getBoolean("is_logged_in", false)

            if (isLoggedIn) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, InitiationActivity::class.java))
            }

            finish()
        }
    }
}
package com.mobile.app_iara.ui.start

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mobile.app_iara.R

class EmailSentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_email_sent)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btVoltarLoginEsqSenha = findViewById<Button>(R.id.btVoltarLoginEsqSenha)
        val tvEmail = findViewById<TextView>(R.id.textView3)
        val tvHelp = findViewById<TextView>(R.id.textView8)

        val userEmail = intent.getStringExtra("USER_EMAIL")

        if (userEmail != null) {
            tvEmail.text = userEmail
        }

        btVoltarLoginEsqSenha.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
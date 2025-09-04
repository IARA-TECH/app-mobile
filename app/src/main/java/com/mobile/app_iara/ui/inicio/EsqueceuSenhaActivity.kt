package com.mobile.app_iara.ui.inicio

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mobile.app_iara.R

class EsqueceuSenhaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_esqueceu_senha)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnVoltar = findViewById<ImageButton>(R.id.btnVoltar)
        val btnVerificarEmail = findViewById<Button>(R.id.btVerificarEmail)

        btnVoltar.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnVerificarEmail.setOnClickListener {
            val intent = Intent(this, EmailEnviadoActivity::class.java)
            startActivity(intent)
        }


    }
}
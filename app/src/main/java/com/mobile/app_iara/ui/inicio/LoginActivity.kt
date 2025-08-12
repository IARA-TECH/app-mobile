package com.mobile.app_iara.ui.inicio

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.mobile.app_iara.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnIniciar = findViewById<ImageButton>(R.id.btnVoltar)
        val edtSenha = findViewById<TextInputEditText>(R.id.senhaInput)
        val btnToggle = findViewById<ImageButton>(R.id.btnToggleSenha)
        var senhaVisivel = false

        btnIniciar.setOnClickListener {
            val intent = Intent(this, InitiationActivity::class.java)
            startActivity(intent)
        }

        btnToggle.setOnClickListener {
            senhaVisivel = !senhaVisivel

            if (senhaVisivel) {
                edtSenha.transformationMethod = HideReturnsTransformationMethod.getInstance()
                btnToggle.setImageResource(R.drawable.olhoaberto)
            } else {
                edtSenha.transformationMethod = PasswordTransformationMethod.getInstance()
                btnToggle.setImageResource(R.drawable.olhofechado)
            }

            edtSenha.setSelection(edtSenha.text?.length ?: 0)
        }

    }
}
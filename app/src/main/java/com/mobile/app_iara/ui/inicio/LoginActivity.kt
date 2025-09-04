package com.mobile.app_iara.ui.inicio

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.mobile.app_iara.R
import com.mobile.app_iara.ui.erros.ErroWifiActivity
import com.mobile.app_iara.utils.NetworkUtils

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔍 Verifica conexão antes de carregar layout
        if (!NetworkUtils.isInternetAvailable(this)) {
            startActivity(Intent(this, ErroWifiActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnVoltar = findViewById<ImageButton>(R.id.btnVoltar)
        val edtSenha = findViewById<TextInputEditText>(R.id.senhaInput)
        val btnToggle = findViewById<ImageButton>(R.id.btnToggleSenha)
        var senhaVisivel = false

        btnVoltar.setOnClickListener {
            val intent = Intent(this, InitiationActivity::class.java)
            startActivity(intent)
            finish()
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

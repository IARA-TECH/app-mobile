package com.mobile.app_iara.ui.inicio

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.mobile.app_iara.MainActivity
import com.mobile.app_iara.utils.NetworkUtils
import com.mobile.app_iara.R
import com.mobile.app_iara.ui.erros.ErroWifiActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        val tvEsqueceuSenha = findViewById<TextView>(R.id.textView5)
        val btnVoltar = findViewById<ImageButton>(R.id.btnVoltar)
        val edtSenha = findViewById<TextInputEditText>(R.id.senhaInput)
        val btnToggle = findViewById<ImageButton>(R.id.btnToggleSenha)
        val btnAvancarLogin = findViewById<Button>(R.id.btnAvancar)
        val emailEditText = findViewById<TextInputEditText>(R.id.editTextEmailOuCpf)
        val checkLogado = findViewById<CheckBox>(R.id.checkBox2)
        var senhaVisivel = false

        val sharedPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)

        btnVoltar.setOnClickListener {
            val intent = Intent(this, InitiationActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnAvancarLogin.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val senha = edtSenha.text.toString().trim()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔍 Verifica conexão antes de autenticar
            if (!NetworkUtils.isInternetAvailable(this)) {
                val intent = Intent(this, ErroWifiActivity::class.java)
                startActivity(intent)
                finish()
                return@setOnClickListener
            }

            // Se tem internet → tenta login no Firebase
            auth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        if (checkLogado.isChecked) {
                            sharedPrefs.edit().putBoolean("is_logged_in", true).apply()
                        } else {
                            sharedPrefs.edit().putBoolean("is_logged_in", false).apply()
                        }

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Falha no login: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
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

        tvEsqueceuSenha.setOnClickListener {
            val intent = Intent(this, EsqueceuSenhaActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        val sharedPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val manterLogado = sharedPrefs.getBoolean("is_logged_in", false)
        val userLogin = auth.currentUser

        // 🔍 Também checa conexão antes de auto-login
        if (userLogin != null && manterLogado) {
            if (!NetworkUtils.isInternetAvailable(this)) {
                val intent = Intent(this, ErroWifiActivity::class.java)
                startActivity(intent)
                finish()
                return
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

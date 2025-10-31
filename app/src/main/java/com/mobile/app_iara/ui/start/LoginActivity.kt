package com.mobile.app_iara.ui.start

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.mobile.app_iara.MainActivity
import com.mobile.app_iara.R
import com.mobile.app_iara.data.model.request.EmailRequest
import com.mobile.app_iara.data.repository.UserRepository
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.util.NetworkUtils
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val userRepository = UserRepository()

    companion object {
        const val PREFS_NAME = "IARA_APP_POSTECH_PREFS"
        const val KEY_FACTORY_ID = "USER_FACTORY_ID"
        const val KEY_LOGGED_IN = "is_logged_in"
    }

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

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d("LoginGoogle", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w("LoginGoogle", "Google sign in failed", e)
                Toast.makeText(this, "Erro no login com Google", Toast.LENGTH_SHORT).show()
            }
        }

        val sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        val btnGoogle = findViewById<ImageButton>(R.id.btnGoogle)
        val tvEsqueceuSenha = findViewById<TextView>(R.id.textView5)
        val btnVoltar = findViewById<ImageButton>(R.id.btnVoltar)
        val edtSenha = findViewById<TextInputEditText>(R.id.senhaInput)
        val btnToggle = findViewById<ImageButton>(R.id.btnToggleSenha)
        val btnAvancarLogin = findViewById<Button>(R.id.btnAvancar)
        val emailEditText = findViewById<TextInputEditText>(R.id.editTextEmailOuCpf)
        val checkLogado = findViewById<CheckBox>(R.id.checkBox2)
        var senhaVisivel = false
        val primeiroAcesso = findViewById<TextView>(R.id.PrimeiroAcesso)

        btnVoltar.setOnClickListener {
            val intent = Intent(this, InitiationActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnGoogle.setOnClickListener {
            googleSignInClient.signOut().addOnCompleteListener {
                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            }
        }

        primeiroAcesso.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
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

            if (!NetworkUtils.isInternetAvailable(this)) {
                val intent = Intent(this, WifiErrorActivity::class.java)
                startActivity(intent)
                finish()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful && auth.currentUser != null) {
                        fetchApiDataAndNavigate(auth.currentUser!!, checkLogado.isChecked)
                    } else {
                        Toast.makeText(this, "Falha no login: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        btnToggle.setOnClickListener {
            senhaVisivel = !senhaVisivel
            if (senhaVisivel) {
                edtSenha.transformationMethod = HideReturnsTransformationMethod.getInstance()
                btnToggle.setImageResource(R.drawable.ic_open_eye)
            } else {
                edtSenha.transformationMethod = PasswordTransformationMethod.getInstance()
                btnToggle.setImageResource(R.drawable.ic_close_eye)
            }
            edtSenha.setSelection(edtSenha.text?.length ?: 0)
        }

        tvEsqueceuSenha.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful && auth.currentUser != null) {
                    fetchApiDataAndNavigate(auth.currentUser!!, true)
                } else {
                    Toast.makeText(this, "Falha na autenticação com Google.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun fetchApiDataAndNavigate(firebaseUser: FirebaseUser, keepLoggedIn: Boolean) {
        val userEmail = firebaseUser.email
        if (userEmail.isNullOrEmpty()) {
            Toast.makeText(this, "Erro: E-mail não encontrado no Firebase.", Toast.LENGTH_SHORT).show()
            auth.signOut()
            return
        }

        lifecycleScope.launch {
            try {
                val userResponse = userRepository.getUserProfileByEmail(EmailRequest(userEmail))

                if (userResponse.isSuccessful && userResponse.body() != null) {
                    val factoryId = userResponse.body()!!.factoryId

                    val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    prefs.edit()
                        .putInt(KEY_FACTORY_ID, factoryId)
                        .putBoolean(KEY_LOGGED_IN, keepLoggedIn)
                        .apply()

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(this@LoginActivity, "Usuário não encontrado em nossa base de dados.", Toast.LENGTH_LONG).show()
                    auth.signOut()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Falha de conexão: ${e.message}", Toast.LENGTH_LONG).show()
                auth.signOut()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val manterLogado = sharedPrefs.getBoolean(KEY_LOGGED_IN, false)
        val userLogin = auth.currentUser

        if (userLogin != null && manterLogado) {
            if (!NetworkUtils.isInternetAvailable(this)) {
                val intent = Intent(this, WifiErrorActivity::class.java)
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
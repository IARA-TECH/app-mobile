package com.mobile.app_iara.ui.start

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
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
import com.google.firebase.auth.GoogleAuthProvider
import com.mobile.app_iara.ui.MainActivity
import com.mobile.app_iara.util.NetworkUtils
import com.mobile.app_iara.R
import com.mobile.app_iara.data.model.request.EmailRequest
import com.mobile.app_iara.data.remote.UserCredentialsHolder
import com.mobile.app_iara.data.repository.UserRepository
import com.mobile.app_iara.ui.error.WifiErrorActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val userRepository = UserRepository()
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        UserCredentialsHolder.clear()

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
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Erro no login com Google", Toast.LENGTH_SHORT).show()
            }
        }

        sharedPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)

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
        val mensagemCampos = findViewById<TextView>(R.id.MensagemCampos)
        val mensagemCredenciais = findViewById<TextView>(R.id.MensagemCredenciais)

        val errorClearTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mensagemCampos.visibility = View.GONE
                mensagemCredenciais.visibility = View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        emailEditText.addTextChangedListener(errorClearTextWatcher)
        edtSenha.addTextChangedListener(errorClearTextWatcher)

        btnVoltar.setOnClickListener {
            startActivity(Intent(this, InitiationActivity::class.java))
            finish()
        }

        btnGoogle.setOnClickListener {
            googleSignInClient.signOut().addOnCompleteListener {
                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            }
        }

        primeiroAcesso.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
            finish()
        }

        btnAvancarLogin.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val senha = edtSenha.text.toString().trim()

            if (email.isEmpty() || senha.isEmpty()) {
                mensagemCampos.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (!NetworkUtils.isInternetAvailable(this)) {
                startActivity(Intent(this, WifiErrorActivity::class.java))
                finish()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        UserCredentialsHolder.setCredentials(email, senha)

                        lifecycleScope.launch {
                            try {
                                val userResponse = userRepository.getUserProfileByEmail(EmailRequest(email))

                                if (userResponse.isSuccessful && userResponse.body() != null) {
                                    val factoryId = userResponse.body()!!.factoryId
                                    val userId = userResponse.body()!!.id

                                    if (checkLogado.isChecked) {
                                        val editor = sharedPrefs.edit()
                                        editor.putBoolean("is_logged_in", true)
                                        editor.putString("email", email)
                                        editor.putString("password", senha)
                                        editor.putInt("key_factory_id", factoryId)
                                        editor.putString("user_id", userId)
                                        editor.apply()
                                    }

                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                    finish()
                                } else {
                                    Toast.makeText(this@LoginActivity, "Usuário não encontrado em nossa base de dados.", Toast.LENGTH_LONG).show()
                                    auth.signOut()
                                    UserCredentialsHolder.clear()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(this@LoginActivity, "Falha de conexão: ${e.message}", Toast.LENGTH_LONG).show()
                                auth.signOut()
                                UserCredentialsHolder.clear()
                            }
                        }
                    } else {
                        mensagemCredenciais.visibility = View.VISIBLE
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
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val userEmail = auth.currentUser?.email
                        if (userEmail.isNullOrEmpty()) {
                            Toast.makeText(this, "Erro: E-mail não encontrado no Firebase.", Toast.LENGTH_SHORT).show()
                            auth.signOut()
                            return@addOnCompleteListener
                        }

                        lifecycleScope.launch {
                            try {
                                val userResponse = userRepository.getUserProfileByEmail(EmailRequest(userEmail))
                                if (userResponse.isSuccessful && userResponse.body() != null) {
                                    val factoryId = userResponse.body()!!.factoryId

                                    val editor = sharedPrefs.edit()
                                    editor.putBoolean("is_logged_in", true)
                                        .remove("email")
                                        .remove("password")
                                        .putInt("key_factory_id", factoryId)
                                        .putString("user_id", userResponse.body()!!.id)
                                        .apply()

                                    UserCredentialsHolder.clear()
                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
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
                    } else {
                        Toast.makeText(this@LoginActivity, "Usuário não encontrado em nossa base de dados.", Toast.LENGTH_LONG).show()
                        auth.signOut()
                    }
                }
        } catch (e: Exception) {
            Toast.makeText(this@LoginActivity, "Falha de conexão: ${e.message}", Toast.LENGTH_LONG).show()
            auth.signOut()
        }
    }

    override fun onStart() {
        super.onStart()
        val sharedPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val manterLogado = sharedPrefs.getBoolean("is_logged_in", false)
        val userLogin = auth.currentUser

        if (userLogin != null && manterLogado) {
            if (!NetworkUtils.isInternetAvailable(this)) {
                startActivity(Intent(this, WifiErrorActivity::class.java))
                finish()
                return
            }
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
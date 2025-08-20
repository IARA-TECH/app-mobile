package com.mobile.app_iara.ui.registroabaco

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mobile.app_iara.MainActivity
import com.mobile.app_iara.R
import com.mobile.app_iara.ui.home.HomeFragment
import com.mobile.app_iara.ui.inicio.LoginActivity
import kotlinx.coroutines.MainScope

class OnboardingActivity : AppCompatActivity() {

    private lateinit var titulo: TextView
    private lateinit var subtitulo: TextView
    private lateinit var imagem: ImageView
    private lateinit var btnProximo: Button
    private lateinit var btnCancelar: Button

    private lateinit var dot1: ImageView
    private lateinit var dot2: ImageView
    private lateinit var dot3: ImageView

    private var pagina = 0

    private val titulos = arrayOf(
        "Automatize sua contagem com uma foto",
        "Tire uma boa foto do ábaco",
        "Transformando em dados"
    )

    private val subtitulos = arrayOf(
        "Com apenas uma imagem do ábaco, o app identifica os dados e cria uma planilha para você automaticamente.",
        "Mantenha a câmera reta, boa iluminação e mostre todas as colunas e miçangas.",
        "A foto vira uma contagem precisa e uma planilha Excel pronta para análise."
    )

    private val imagens = arrayOf(
        R.drawable.mulherregistrandoabaco,
        R.drawable.celular,
        R.drawable.analise
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        titulo = findViewById(R.id.textView6)
        subtitulo = findViewById(R.id.textView13)
        imagem = findViewById(R.id.imageView4)
        btnProximo = findViewById(R.id.btnProximoOnboarding)
        btnCancelar = findViewById(R.id.btnCancelarOnboarding)

        dot1 = findViewById(R.id.dot1)
        dot2 = findViewById(R.id.dot2)
        dot3 = findViewById(R.id.dot3)

        btnProximo.setOnClickListener {
            if (pagina < 2) {
                pagina++
                updatePage()
            } else {
                finish()
            }
        }

        btnCancelar.setOnClickListener {
            if (pagina > 0) {
                pagina--
                updatePage()
                if (pagina == 0) {
                    btnCancelar.text = "Cancelar"
                }
            } else {
                val intent = Intent(this, MainActivity::class.java) // troca pelo nome da sua Activity principal
                startActivity(intent)
                finish()
            }
        }

    }

    private fun updatePage() {
        titulo.text = titulos[pagina]
        subtitulo.text = subtitulos[pagina]
        imagem.setImageResource(imagens[pagina])

        dot1.setImageResource(if (pagina == 0) R.drawable.dot_active else R.drawable.dot_inactive)
        dot2.setImageResource(if (pagina == 1) R.drawable.dot_active else R.drawable.dot_inactive)
        dot3.setImageResource(if (pagina == 2) R.drawable.dot_active else R.drawable.dot_inactive)

        btnCancelar.text = if (pagina == 0) "Cancelar" else "Anterior"
    }
}


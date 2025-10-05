package com.mobile.app_iara.ui.abacus.confirmation

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.R

class AbacusConfirmationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_abacus_confirmation)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerInformacoes)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val lista = listOf(
            Line("1. LoremIpsum", "#32A852", 13),
            Line("2. LoremIpsum", "#D00EE6", 31),
            Line("2. LoremIpsum", "#AD2D3C", 65),
        )

        val adapter = AbacusConfirmationAdapter(lista)
        recyclerView.adapter = adapter

//        btnCancelar.setOnClickListener {
//            if (ultimoPublicId != null) {
//                deletarDoCloudinary(ultimoPublicId!!)
//            }
//            finish()
//        }

    }

    private fun deletarDoCloudinary(publicId: String) {
        Thread {
            try {
                val config = mapOf(
                    "cloud_name" to "abacus_photos",
                    "api_key" to "SUA_API_KEY",
                    "api_secret" to "SEU_API_SECRET"
                )
                val cloudinary = com.cloudinary.Cloudinary(config)
                cloudinary.uploader().destroy(publicId, mapOf("invalidate" to true))
                Log.d("Cloudinary", "Imagem deletada com sucesso: $publicId")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

}
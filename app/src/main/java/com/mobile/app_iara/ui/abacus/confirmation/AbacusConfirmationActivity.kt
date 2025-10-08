package com.mobile.app_iara.ui.abacus.confirmation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.R
import com.mobile.app_iara.ui.camera.ErrorProcessingActivity
import com.mobile.app_iara.ui.camera.ProcessingActivity

class AbacusConfirmationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_abacus_confirmation)

        val erro = true;

        val imageUriString = intent.getStringExtra("image_uri")
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            Log.d("Confirmation", "URI recebida: $imageUri")
        }

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

        val btnConfirmar = findViewById<Button>(R.id.btnConfirmar)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)

        btnCancelar.setOnClickListener {
            finish()
        }

        btnConfirmar.setOnClickListener {
            // TODO: Lembrar de implementar lógica de verdade de erro
            if(erro){
                val intent = Intent(this, ErrorProcessingActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, ProcessingActivity::class.java)
                startActivity(intent)
                finish()
            }
        }


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
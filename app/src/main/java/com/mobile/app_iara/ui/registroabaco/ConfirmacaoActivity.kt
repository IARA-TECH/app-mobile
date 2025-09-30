package com.mobile.app_iara.ui.registroabaco

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.R
import com.mobile.app_iara.ui.profile.termsandprivacy.PrivacidadeAdapter
import com.mobile.app_iara.ui.profile.termsandprivacy.Termo

class ConfirmacaoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_confirmacao)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerInformacoes)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val lista = listOf(
            Informacao("1. LoremIpsum", "#32A852", 13),
            Informacao("2. LoremIpsum", "#D00EE6", 31),
            Informacao("2. LoremIpsum", "#AD2D3C", 65),

        )

        val adapter = ConfirmacaoAdapter(lista)
        recyclerView.adapter = adapter
    }
}
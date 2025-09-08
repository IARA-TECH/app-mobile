package com.mobile.app_iara.ui.profile.faq

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.R
import com.mobile.app_iara.ui.inicio.InitiationActivity

class FaqActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_faq)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnVoltar = findViewById<ImageButton>(R.id.btnVoltar4)
        btnVoltar.setOnClickListener {
            val intent = Intent(this, InitiationActivity::class.java)
            startActivity(intent)
        }

        val recyclerViewDuvidas = findViewById<RecyclerView>(R.id.recycler_duvidas)
        val recyclerViewPopulares = findViewById<RecyclerView>(R.id.recycler_populares)

        recyclerViewPopulares.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewDuvidas.layoutManager = LinearLayoutManager(this)

        val listaPopulares = listOf(
            Popular("1. LoremIpsum", "Lorem ipsum dolor sit amet, consectetur adipiscing elit...Lorem ipsum dolor sit amet, consectetur adipiscing elit..."),
            Popular("1. LoremIpsum", "Lorem ipsum dolor sit amet, consectetur adipiscing elit...Lorem ipsum dolor sit amet, consectetur adipiscing elit..."),
            Popular("1. LoremIpsum", "Lorem ipsum dolor sit amet, consectetur adipiscing elit...Lorem ipsum dolor sit amet, consectetur adipiscing elit...")
            )

        val adapterPopulares = PopularAdapter(listaPopulares)
        recyclerViewPopulares.adapter = adapterPopulares

        val listaDuvidas = listOf(
            Duvida("1. LoremIpsum", "Lorem ipsum dolor sit amet, consectetur adipiscing elit...Lorem ipsum dolor sit amet, consectetur adipiscing elit..."),
            Duvida("1. LoremIpsum", "Lorem ipsum dolor sit amet, consectetur adipiscing elit..."),
            Duvida("1. LoremIpsum", "Lorem ipsum dolor sit amet, consectetur adipiscing elit...Lorem ipsum dolor sit amet, consectetur adipiscing elit...Lorem ipsum dolor sit amet, consectetur adipiscing elit..."),
        )

        val adapterDuvidas = DuvidasAdapter(listaDuvidas)
        recyclerViewDuvidas.adapter = adapterDuvidas
    }
}
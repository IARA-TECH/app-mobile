package com.mobile.app_iara.ui.profile.termsandprivacy

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.R

class PrivacyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_privacy)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val btnTermos = findViewById<Button>(R.id.btnTermos)
        val btnVoltar = findViewById<ImageButton>(R.id.btnVoltar3)

        btnTermos.setOnClickListener {
            val intent = Intent(this, TermsActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(this, 0,0)
            startActivity(intent, options.toBundle())
            finish()
        }

        btnVoltar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)

        val lista = listOf(
            Terms("1. LoremIpsum", "Lorem ipsum dolor sit amet, consectetur adipiscing elit..."),
            Terms("2. LoremIpsum", "Mais texto aqui..."),
            Terms("3. LoremIpsum", "Outro parágrafo..."),
            Terms("3. LoremIpsum", "Outro parágrafo..."),
            Terms("3. LoremIpsum", "Outro parágrafo..."),
            Terms("3. LoremIpsum", "Outro parágrafo..."),
            Terms("3. LoremIpsum", "Outro parágrafo..."),
            Terms("3. LoremIpsum", "Outro parágrafo..."),
            Terms("3. LoremIpsum", "Outro parágrafo..."),
            Terms("3. LoremIpsum", "Outro parágrafo..."),
            Terms("3. LoremIpsum", "Outro parágrafo..."),
            Terms("3. LoremIpsum", "Outro parágrafo..."),
            Terms("3. LoremIpsum", "Outro parágrafo..."),
            Terms("3. LoremIpsum", "Outro parágrafo..."),
            Terms("3. LoremIpsum", "Outro parágrafo..."),
            Terms("3. LoremIpsum", "Outro parágrafo..."),
            Terms("3. LoremIpsum", "Outro parágrafo...")

        )


        val adapter = PrivacyAdapter(lista)
        recyclerView.adapter = adapter
    }
}

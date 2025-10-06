package com.mobile.app_iara.ui.home.history

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.app_iara.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupBackButton()

        val historyData = createDummyData()
        setupRecyclerView(historyData)
    }

    private fun setupRecyclerView(historyList: List<AbacusHistory>) {
        val historyAdapter = AbacusHistoryAdapter(historyList)
        binding.historyRecyclerView.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(this@HistoryActivity)
        }
    }

    private fun createDummyData(): List<AbacusHistory> {
        return listOf(
            AbacusHistory(
                urlPhoto = "https://picsum.photos/id/10/200/200",
                titulo = "Ábaco de Consumo de Ração",
                name = "Carlos Homero",
                approve = "Júlio Carneiro",
                timestamp = "23/10/2025 - 10:45"
            ),
            AbacusHistory(
                urlPhoto = "https://picsum.photos/id/20/200/200",
                titulo = "Ábaco de Conversão Alimentar (CA)",
                name = "Julia Maria",
                approve = "Carla de Jesus",
                timestamp = "23/10/2025 - 09:45"
            ),
            AbacusHistory(
                urlPhoto = "https://picsum.photos/id/30/200/200",
                titulo = "Ábaco de Índice de Eficiência Produtiva",
                name = "Julia Maria",
                approve = "Júlio Carneiro",
                timestamp = "26/10/2025 - 10:10"
            ),
            AbacusHistory(
                urlPhoto = "https://picsum.photos/id/40/200/200",
                titulo = "Ábaco de Espaço por Ave",
                name = "Lucas Fernando Souza",
                approve = "Carla de Jesus",
                timestamp = "28/10/2025 14:00"
            ),
            AbacusHistory(
                urlPhoto = "https://picsum.photos/id/10/200/200",
                titulo = "Ábaco de Consumo de Ração",
                name = "Carlos Homero",
                approve = "Lucas Fernando Souza",
                timestamp = "29/10/2025 - 10:27"
            )
        )
    }

    private fun setupBackButton() {
        binding.included.imgBack.setOnClickListener {
            finish()
        }
    }
}
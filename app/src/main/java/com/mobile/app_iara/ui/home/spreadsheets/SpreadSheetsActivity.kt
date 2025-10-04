package com.mobile.app_iara.ui.home.spreadsheets

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.app_iara.R
import com.mobile.app_iara.databinding.ActivitySpreadSheetsBinding

class SpreadSheetsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySpreadSheetsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        binding = ActivitySpreadSheetsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val spreadSheetsData = createSpreadSheetsDummyData()
        setupRecyclerView(spreadSheetsData)
    }

    private fun setupRecyclerView(historyList: List<SpreadSheets>) {
        val spreadSheetsAdapter = SpreadSheetsAdapter(historyList)
        binding.spreadSheetsRecyclerView.apply {
            adapter = spreadSheetsAdapter
            layoutManager = LinearLayoutManager(this@SpreadSheetsActivity)
        }
    }

    private fun createSpreadSheetsDummyData(): List<SpreadSheets> {
        return listOf(
            SpreadSheets(
                title = "Ábaco post-mortem - Lote A1",
                date = "02/10/2025",
                urlSpreadSheet = "url example"
            ),
            SpreadSheets(
                title = "Relatório de Perda - Silo 3",
                date = "29/09/2025",
                urlSpreadSheet = "url example"
            ),
            SpreadSheets(
                title = "Análise de Mortalidade - Aviário 5",
                date = "25/09/2025",
                urlSpreadSheet = "url example"
            ),
            SpreadSheets(
                title = "Inspeção de Qualidade - Lote B7",
                date = "18/09/2025",
                urlSpreadSheet = "url example"
            ),
            SpreadSheets(
                title = "Relatório de Desempenho - Setor Norte",
                date = "15/09/2025",
                urlSpreadSheet = "url example"
            )
        )
    }
}
package com.mobile.app_iara.ui.abacus.confirmation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.R
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.util.NetworkUtils


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

        if (!NetworkUtils.isInternetAvailable(this)) {
            val intent = Intent(this, WifiErrorActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        val imageUriString = intent.getStringExtra("image_uri")
        val csvData = intent.getStringExtra("csv_data")

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerInformacoes)


        recyclerView.layoutManager = LinearLayoutManager(this)

        val dataList: List<Line>

        if (csvData != null && csvData.isNotBlank()) {
            Log.d("Confirmation", "Processando CSV: $csvData")
            dataList = parseCsvData(csvData)
        } else {
            Log.w("Confirmation", "Dados CSV nulos ou vazios, usando lista de erro.")
            dataList = getErrorList()
        }

        Log.d("Confirmation", "Itens processados para o adapter: ${dataList.size}")

        val adapter = AbacusConfirmationAdapter(dataList)
        recyclerView.adapter = adapter
    }

    private fun parseCsvData(csvData: String): List<Line> {
        val parsedLines = mutableListOf<Line>()
        try {
            val lines = csvData.split("\n")
                .filter { it.isNotBlank() }
                .drop(1)

            for (line in lines) {
                val parts = line.split(",")

                if (parts.size >= 3) {
                    val category = parts[0].trim()
                    val title = parts[1].trim()
                    val quantity = parts[2].trim().toIntOrNull() ?: 0

                    parsedLines.add(Line(category = category, title = title, value = quantity))

                } else {
                    Log.w("parseCsvData", "Linha CSV mal formatada: $line")
                }
            }
        } catch (e: Exception) {
            Log.e("Confirmation", "Erro ao processar CSV", e)
            parsedLines.clear()
            parsedLines.add(Line("Erro", "Erro ao processar CSV", 0))
        }

        if (parsedLines.isEmpty()) {
            Log.w("parseCsvData", "Nenhuma linha de dados encontrada.")
            return getErrorList("Nenhuma linha de dados encontrada")
        }

        return parsedLines
    }

    private fun getErrorList(message: String = "Erro ao processar CSV"): List<Line> {
        return listOf(
            Line("Erro", message, 0),
            Line("Erro", "Verifique a API ou a foto", 0)
        )
    }
}
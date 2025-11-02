package com.mobile.app_iara.ui.abacus.confirmation

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.R
import com.mobile.app_iara.data.repository.AbacusPhotoRepository
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.util.NetworkUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class AbacusConfirmationActivity : AppCompatActivity() {

    private val viewModel: AbacusConfirmationViewModel by viewModels()
    private val repository = AbacusPhotoRepository()
    private lateinit var prefs: SharedPreferences
    private lateinit var btnConfirmar: Button
    private lateinit var btnCancelar: Button

    private var imageUriString: String? = null
    private var csvData: String? = null
    private var abacusId: String? = null
    private var factoryId: Int = -1
    private var dataList: MutableList<Line> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_abacus_confirmation)

        prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

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

        imageUriString = intent.getStringExtra("image_uri")
        csvData = intent.getStringExtra("csv_data")
        abacusId = intent.getStringExtra("ABACUS_ID")
        factoryId = intent.getIntExtra("FACTORY_ID", -1)


        btnConfirmar = findViewById(R.id.button4)
        btnCancelar = findViewById(R.id.button5)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerInformacoes)

        recyclerView.layoutManager = LinearLayoutManager(this)

        if (csvData != null && csvData!!.isNotBlank()) {
            Log.d("Confirmation", "Processando CSV: $csvData")
            dataList = parseCsvData(csvData!!)
        } else {
            Log.w("Confirmation", "Dados CSV nulos ou vazios, usando lista de erro.")
        }

        Log.d("Confirmation", "Itens processados para o adapter: ${dataList.size}")

        val adapter = AbacusConfirmationAdapter(dataList)
        recyclerView.adapter = adapter

        btnCancelar.setOnClickListener{
            finish()
        }

        btnConfirmar.setOnClickListener {
            handleConfirmClick()
        }

        observeViewModelState()
    }

    private fun handleConfirmClick() {
        val takenBy = prefs.getString("user_id", null)
        val shiftId = repository.getShiftId()

        if (takenBy == null) {
            Toast.makeText(this, "Erro: Usuário não logado.", Toast.LENGTH_SHORT).show()
            return
        }
        if (factoryId == -1 || abacusId == null) {
            Toast.makeText(this, "Erro: ID do Ábaco ou Fábrica perdido.", Toast.LENGTH_SHORT).show()
            return
        }
        if (imageUriString == null || csvData == null) {
            Toast.makeText(this, "Erro: Foto ou CSV não encontrados.", Toast.LENGTH_SHORT).show()
            return
        }

        val finalCsv = regenerateCsvForUpload(dataList)
        if (finalCsv.isBlank()) {
            Toast.makeText(this, "Erro: Não há dados para enviar.", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.confirmUpload(
            context = applicationContext,
            factoryId = factoryId,
            shiftId = shiftId,
            takenBy = takenBy,
            abacusId = abacusId!!,
            imageUriString = imageUriString!!,
            csvData = finalCsv!!
        )
    }

    private fun regenerateCsvForUpload(list: List<Line>): String {
        val builder = StringBuilder()

        builder.append("Tipo da condena,Categoria,Quantidade de miçangas MOVIDAS\n")

        list.forEach { line ->
            builder.append("${line.category},${line.title},${line.value}\n")
        }

        return builder.toString().trim()
    }

    private fun observeViewModelState() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is ConfirmationState.Idle -> {
                        btnConfirmar.isEnabled = true
                    }
                    is ConfirmationState.Loading -> {
                        btnConfirmar.isEnabled = false
                    }
                    is ConfirmationState.Success -> {
                        btnConfirmar.isEnabled = true
                        Log.d("Confirmation", "Foto: ${state.data.photoUrl}, Planilha: ${state.data.sheetUrl}")
                        showSuccessSheet()
                    }
                    is ConfirmationState.Error -> {
                        btnConfirmar.isEnabled = true
                        showErrorSheet()
                    }
                }
            }
        }
    }

    private fun parseCsvData(csvData: String): MutableList<Line> {
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
        }

        return parsedLines
    }

    private fun showSuccessSheet() {
        val successSheet = SpreadsheetSuccess()
        successSheet.isCancelable = false
        successSheet.show(supportFragmentManager, "SuccessSheet")
    }

    private fun showErrorSheet() {
        val errorSheet = SpreadsheetSuccess()
        errorSheet.isCancelable = true
        errorSheet.show(supportFragmentManager, "error_sheet")
    }

}
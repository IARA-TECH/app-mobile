package com.mobile.app_iara.ui.spreadsheets

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mobile.app_iara.data.repository.SpreadsheetRepository
import com.mobile.app_iara.util.DataUtil
import kotlinx.coroutines.launch

sealed class SpreadsheetUiState {
    object Loading : SpreadsheetUiState()
    data class Success(val spreadsheets: List<SpreadSheets>) : SpreadsheetUiState()
    data class Error(val message: String) : SpreadsheetUiState()
}

class SpreadsheetViewModel (application: Application) : AndroidViewModel(application) {

    private val repository = SpreadsheetRepository()
    private val sharedPrefs = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableLiveData<SpreadsheetUiState>()
    val uiState: LiveData<SpreadsheetUiState> = _uiState

    private fun extractSpreadsheetName(url: String?): String {
        if (url.isNullOrEmpty()) {
            return "Título indisponível"
        }

        val cleanUrl = url.substringBefore('?')
        val fullFileName = cleanUrl.substringAfterLast('/')
        val title = fullFileName.substringBeforeLast('.')
        if (title.isBlank()) {
            return "Título indisponível"
        }

        return title
    }

    fun fetchSpreadsheets() {
        _uiState.value = SpreadsheetUiState.Loading

        val factoryId = sharedPrefs.getInt("key_factory_id", -1)

        if (factoryId == -1) {
            _uiState.value = SpreadsheetUiState.Error("ID da fábrica não encontrado. Por favor, faça login novamente.")
            return
        }

        viewModelScope.launch {
            try {
                val response = repository.getSheetsByFactoryId(factoryId)

                if (response.isSuccessful && response.body() != null) {
                    val sheetDataList = response.body()!!.data

                    val spreadSheetsList = sheetDataList.map { sheetData ->
                        SpreadSheets(
                            title = extractSpreadsheetName(sheetData.sheetUrlBlob),
                            date = DataUtil.formatIsoDateToAppDate(sheetData.date),
                            urlSpreadSheet = sheetData.sheetUrlBlob
                        )
                    }
                    _uiState.value = SpreadsheetUiState.Success(spreadSheetsList)
                } else {
                    _uiState.value = SpreadsheetUiState.Error("Erro ao buscar planilhas: ${response.message()}")
                }
            } catch (e: Exception) {
                _uiState.value = SpreadsheetUiState.Error("Falha na conexão: ${e.message}")
            }
        }
    }
}
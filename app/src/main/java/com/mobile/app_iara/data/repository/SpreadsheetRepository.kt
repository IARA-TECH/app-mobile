package com.mobile.app_iara.data.repository

import com.mobile.app_iara.data.model.response.SheetsResponse
import com.mobile.app_iara.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response

class SpreadsheetRepository {

    private val sheetService = RetrofitClient.sheetService

    suspend fun getSheetsByFactoryId(factoryId: Int): Response<SheetsResponse> {
        return sheetService.getSheetsByFactoryId(factoryId)
    }

    suspend fun deleteSheet(sheetId: String): Result<ResponseBody> {
        return withContext(Dispatchers.IO) {
            try {
                val response = sheetService.deleteSheet(sheetId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Erro ao deletar planilha: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
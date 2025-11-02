package com.mobile.app_iara.data.repository

import com.mobile.app_iara.data.model.response.SheetsResponse
import com.mobile.app_iara.data.remote.RetrofitClient
import retrofit2.Response

class SpreadsheetRepository {

    private val sheetService = RetrofitClient.sheetService

    suspend fun getSheetsByFactoryId(factoryId: Int): Response<SheetsResponse> {
        return sheetService.getSheetsByFactoryId(factoryId)
    }
}
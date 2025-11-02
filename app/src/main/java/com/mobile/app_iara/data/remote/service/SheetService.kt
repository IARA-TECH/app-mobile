package com.mobile.app_iara.data.remote.service

import com.mobile.app_iara.data.model.response.SheetsResponse
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Path

interface SheetService {

    @GET("iara/api/sheets")
    suspend fun listAllSheets(): Response<SheetsResponse>

    @GET("iara/api/sheets/factory/{factoryId}")
    suspend fun getSheetsByFactoryId(
        @Path("factoryId") factoryId: Int
    ): Response<SheetsResponse>

    @DELETE("iara/api/sheets/{id}")
    suspend fun deleteSheet(
        @Path("id") sheetId: String
    ): Response<ResponseBody>
}
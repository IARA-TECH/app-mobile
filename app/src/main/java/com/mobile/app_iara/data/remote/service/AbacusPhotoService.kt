package com.mobile.app_iara.data.remote.service

import com.mobile.app_iara.data.model.response.AbacusAnalysisResponse
import com.mobile.app_iara.data.model.response.AbacusConfirmResponse
import com.mobile.app_iara.data.model.response.AbacusPhotoResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface AbacusPhotoService {
    @GET("/iara/api/abacus-photos")
    suspend fun getAbacusPhotos(
        @Query("factoryId") factoryId: Int
    ): Response<AbacusPhotoResponse>

    @Multipart
    @POST("/iara/api/abacus-photos/analyze")
    suspend fun analyzePhoto(
        @Part file: MultipartBody.Part,
        @Query("colors") colors: String? = null,
        @Query("values") values: String? = null
    ): Response<AbacusAnalysisResponse>

    @Multipart
    @POST("iara/api/abacus-photos/confirm")
    suspend fun confirmAndUpload(
        @Query("factoryId") factoryId: Int,
        @Query("shiftId") shiftId: String,
        @Query("takenBy") takenBy: String,
        @Query("abacusId") abacusId: String,
        @Part file: MultipartBody.Part,
        @Part csv: MultipartBody.Part
    ): Response<AbacusConfirmResponse>
}
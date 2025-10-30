package com.mobile.app_iara.data.remote.service

import com.mobile.app_iara.data.model.response.AbacusPhotosApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AbacusPhotoService {
    @GET("/iara/api/abacus-photos")
    suspend fun getAbacusPhotos(
        @Query("factoryId") factoryId: Int
    ): Response<AbacusPhotosApiResponse>
}
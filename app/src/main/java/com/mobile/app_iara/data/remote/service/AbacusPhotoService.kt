package com.mobile.app_iara.data.remote.service

import com.mobile.app_iara.data.model.response.AbacusPhotosApiResponse
import retrofit2.http.GET

interface AbacusPhotoService {
    @GET("iara/api/abacus-photos")
    suspend fun getAllAbacusesPhotos(): AbacusPhotosApiResponse
}
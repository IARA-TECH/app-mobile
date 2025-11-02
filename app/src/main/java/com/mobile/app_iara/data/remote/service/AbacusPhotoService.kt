package com.mobile.app_iara.data.remote.service

import com.mobile.app_iara.data.model.AbacusPhotoData
import com.mobile.app_iara.data.model.response.AbacusPhotoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.DELETE
import com.mobile.app_iara.data.model.request.ValidationRequest
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

interface AbacusPhotoService {
    @GET("/iara/api/abacus-photos")
    suspend fun getAbacusPhotos(
        @Query("factoryId") factoryId: Int
    ): Response<AbacusPhotoResponse>

    @PUT("/iara/api/abacus-photos/validation/{id}")
    suspend fun validateAbacusPhoto(
        @Path("id") photoId: String,
        @Body validationRequest: ValidationRequest
    ): Response<AbacusPhotoData>

    @DELETE("/iara/api/abacus-photos/{id}")
    suspend fun denyAbacusPhoto(
        @Path("id") photoId: String
    ): Response<ResponseBody>
}
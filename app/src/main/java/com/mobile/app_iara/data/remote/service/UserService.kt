package com.mobile.app_iara.data.remote.service

import com.mobile.app_iara.data.model.response.UserProfileResponse
import com.mobile.app_iara.data.model.request.EmailRequest
import com.mobile.app_iara.data.model.request.RegisterCollaboratorRequest
import com.mobile.app_iara.data.model.request.UpdatePhotoRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {

    @POST("users/by-email")
    suspend fun getUserProfileByEmail(@Body request: EmailRequest): Response<UserProfileResponse>

    @PUT("user-photos/by-user/{userId}")
    suspend fun updateUserPhoto(
        @Path("userId") id: String,
        @Body request: UpdatePhotoRequest
    ): Response<Unit>

    @GET("users/by-factory/{factoryId}")
    suspend fun getUsersByFactory(@Path("factoryId") factoryId: Int): Response<List<UserProfileResponse>>

    @POST("users")
    suspend fun registerCollaborator(
        @Body request: RegisterCollaboratorRequest
    ): Response<UserProfileResponse>
}
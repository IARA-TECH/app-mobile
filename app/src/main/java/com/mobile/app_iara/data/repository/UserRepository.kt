package com.mobile.app_iara.data.repository

import android.content.Context
import android.net.Uri
import com.mobile.app_iara.data.model.request.EmailRequest
import com.mobile.app_iara.data.model.request.UpdatePhotoRequest
import com.mobile.app_iara.data.model.response.UserPhotoResponse
import com.mobile.app_iara.data.model.response.UserProfileResponse
import com.mobile.app_iara.data.remote.RetrofitClient
import com.mobile.app_iara.data.remote.SupabaseClientProvider
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.InputStream
import java.util.UUID

class UserRepository {

    private val userService = RetrofitClient.userService

    private val supabase = SupabaseClientProvider.client


    suspend fun getUserProfileByEmail(request: EmailRequest) =
        userService.getUserProfileByEmail(request)

    suspend fun getUserPhoto(userId: String): Response<UserPhotoResponse> {
        return userService.getUserPhoto(userId)
    }

    suspend fun getAllUsers(): Result<List<UserProfileResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userService.getAllUsers()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Erro ao buscar usuários: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun uploadUserPhoto(context: Context, userId: String, photoUri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                val storage = supabase.storage.from("iara_bucket")

                val inputStream: InputStream? = context.contentResolver.openInputStream(photoUri)

                val folder = "user_account_photo"
                val fileName = "${folder}/${userId}_${UUID.randomUUID()}.jpg"

                val bytes = inputStream?.use {
                    it.readBytes()
                }

                if (bytes != null) {
                    storage.upload(fileName, bytes, upsert = true)
                } else {
                    return@withContext null
                }

                storage.publicUrl(fileName)

            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun saveUserPhotoUrl(userId: String, photoUrl: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val request = UpdatePhotoRequest(urlBlob = photoUrl, userId = userId)
                val response = userService.updateUserPhoto(id = userId, request = request)

                response.isSuccessful
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
package com.mobile.app_iara.data.repository

import com.mobile.app_iara.data.model.AbacusPhotoData
import com.mobile.app_iara.data.remote.RetrofitClient
import com.mobile.app_iara.data.remote.service.AbacusPhotoService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.mobile.app_iara.data.model.request.ValidationRequest
import okhttp3.ResponseBody

class AbacusPhotoRepository {

    private val abacusPhotoService: AbacusPhotoService = RetrofitClient.abacusPhotoService

    suspend fun getPendingPhotosByFactory(factoryId: Int): Result<List<AbacusPhotoData>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = abacusPhotoService.getAbacusPhotos(factoryId)

                if (response.isSuccessful) {
                    val photoList = response.body()?.data ?: emptyList()

                    val filteredList = photoList.filter { photo ->
                        photo.validatedBy.isNullOrBlank()
                    }

                    Result.success(filteredList)
                } else {
                    Result.failure(Exception("Erro na resposta da API: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getValidatedPhotosByFactory(factoryId: Int): Result<List<AbacusPhotoData>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = abacusPhotoService.getAbacusPhotos(factoryId)

                if (response.isSuccessful) {
                    val photoList = response.body()?.data ?: emptyList()

                    val filteredList = photoList.filter { photo ->
                        !photo.validatedBy.isNullOrBlank()
                    }

                    Result.success(filteredList)
                } else {
                    Result.failure(Exception("Erro na resposta da API: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun approvePhoto(photoId: String, validatorName: String): Result<AbacusPhotoData> {
        return withContext(Dispatchers.IO) {
            try {
                val request = ValidationRequest(validatedBy = validatorName)
                val response = abacusPhotoService.validateAbacusPhoto(photoId, request)

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Erro ao aprovar foto: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun denyPhoto(photoId: String): Result<ResponseBody> {
        return withContext(Dispatchers.IO) {
            try {
                val response = abacusPhotoService.denyAbacusPhoto(photoId)

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Erro ao negar foto: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
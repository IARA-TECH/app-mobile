package com.mobile.app_iara.data.repository

import com.mobile.app_iara.data.model.AbacusPhotoData
import com.mobile.app_iara.data.remote.RetrofitClient // O import está correto
import com.mobile.app_iara.data.remote.service.AbacusPhotoService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
}
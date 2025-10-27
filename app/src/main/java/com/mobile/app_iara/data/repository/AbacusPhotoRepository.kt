package com.mobile.app_iara.data.repository

import com.mobile.app_iara.data.model.AbacusPhotoData
import com.mobile.app_iara.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AbacusPhotoRepository {
    private val abacusPhotoService = RetrofitClient.abacusPhotosService

    suspend fun getValidatedPhotosByFactory(factoryId: Int): Result<List<AbacusPhotoData>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = abacusPhotoService.getAllAbacusesPhotos()

                val filteredList = response.data.filter { photo ->
                    val matchesFactory = photo.factoryId == factoryId
                    val isValidated = !photo.validatedBy.isNullOrEmpty()

                    matchesFactory && isValidated
                }

                Result.success(filteredList)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
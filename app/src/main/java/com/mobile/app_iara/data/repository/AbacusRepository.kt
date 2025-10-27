package com.mobile.app_iara.data.repository

import com.mobile.app_iara.data.model.AbacusData
import com.mobile.app_iara.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AbacusRepository {
    private val abacusService = RetrofitClient.abacusService

    suspend fun getAbacusesByFactory(factoryId: Int): Result<List<AbacusData>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = abacusService.getAllAbacuses()

                val filteredList = response.data.filter { abacus ->
                    abacus.factoryId == factoryId
                }

                Result.success(filteredList)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
package com.mobile.app_iara.data.repository

import com.mobile.app_iara.data.model.AbacusData
import com.mobile.app_iara.data.model.request.AbacusCreateRequest
import com.mobile.app_iara.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AbacusRepository {
    private val abacusService = RetrofitClient.abacusService

    suspend fun getAbacusesByFactory(factoryId: Int): Result<List<AbacusData>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = abacusService.getAllAbacuses()

                val filteredList = response.data.filter { abacus: AbacusData ->
                    abacus.factoryId == factoryId
                }

                Result.success(filteredList)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteAbacus(abacusId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = abacusService.deleteAbacus(abacusId)
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Falha ao deletar ábaco: Código ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun registerAbacus(abacus: AbacusCreateRequest): Result<AbacusData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = abacusService.createAbacus(abacus)

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!.data)
                } else {
                    Result.failure(Exception("Erro ao cadastrar ábaco: Código ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateAbacus(abacusId: String, request: AbacusCreateRequest): Result<AbacusData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = abacusService.updateAbacus(abacusId, request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Erro ao atualizar ábaco: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
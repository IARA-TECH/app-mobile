package com.mobile.app_iara.data.repository

import com.mobile.app_iara.data.model.response.ShiftListResponse
import com.mobile.app_iara.data.remote.service.ShiftService

class ShiftRepository(private val apiService: ShiftService) {

    suspend fun getAllShifts(): Result<ShiftListResponse> {
        return try {
            val response = apiService.getAllShifts()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erro ao buscar turnos: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Erro de rede: ${e.message}"))
        }
    }
}
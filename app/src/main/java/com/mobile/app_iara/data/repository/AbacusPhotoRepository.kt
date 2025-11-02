package com.mobile.app_iara.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.mobile.app_iara.data.model.AbacusPhotoData
import com.mobile.app_iara.data.remote.RetrofitClient
import com.mobile.app_iara.data.remote.service.AbacusPhotoService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.math.roundToInt

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

    suspend fun analyzeAbacusAndGetCsv(
        imageFile: File,
        colors: String? = null,
        values: String? = null
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val originalBitmap = BitmapFactory.decodeFile(imageFile.path)
                val resizedBitmap = resizeBitmap(originalBitmap, 1080)
                val outputStream = ByteArrayOutputStream()
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
                val compressedData = outputStream.toByteArray()

                val requestFile = compressedData.toRequestBody(
                    "image/jpeg".toMediaTypeOrNull()
                )


                val imagePart = MultipartBody.Part.createFormData(
                    "file",
                    imageFile.name,
                    requestFile
                )

                val response = abacusPhotoService.analyzePhoto(
                    file = imagePart,
                    colors = colors,
                    values = values
                )
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null) {
                        if (responseBody.status == 200) {
                            Result.success(responseBody.data.resultadoCsv)
                        } else {
                            Result.failure(Exception("Análise falhou: ${responseBody.message}"))
                        }
                    } else {
                        Result.failure(Exception("Resposta vazia da API"))
                    }
                } else {
                    Result.failure(Exception("Erro HTTP: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun resizeBitmap(source: Bitmap, maxWidth: Int): Bitmap {
        if (source.width <= maxWidth) {
            return source
        }

        val ratio = source.width.toFloat() / source.height.toFloat()
        val targetWidth = maxWidth
        val targetHeight = (targetWidth / ratio).roundToInt()

        return Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true)
    }
}

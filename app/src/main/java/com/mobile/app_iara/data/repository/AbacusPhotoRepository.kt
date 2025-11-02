package com.mobile.app_iara.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.mobile.app_iara.data.model.AbacusPhotoData
import com.mobile.app_iara.data.model.response.AbacusConfirmData
import com.mobile.app_iara.data.remote.RetrofitClient
import com.mobile.app_iara.data.remote.service.AbacusPhotoService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Calendar
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

    private fun compressImageFile(imageFile: File): ByteArray {
        val originalBitmap = BitmapFactory.decodeFile(imageFile.path)
        val resizedBitmap = resizeBitmap(originalBitmap, 1080) // Redimensiona
        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream) // Comprime
        return outputStream.toByteArray()
    }

    fun getShiftId(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 7..11 -> "690499fa7983b1102be81579"
            in 12..17 -> "690499fb7983b1102be8157a"
            else -> "690499fb7983b1102be8157b"
        }
    }

    private fun createCsvRequestBody(csvData: String): RequestBody {
        return csvData.toRequestBody("text/csv".toMediaTypeOrNull())
    }

    suspend fun confirmAndUploadData(
        context: Context,
        factoryId: Int,
        shiftId: String,
        takenBy: String,
        abacusId: String,
        imageUriString: String,
        csvData: String
    ): Result<AbacusConfirmData> {
        return withContext(Dispatchers.IO) {
            try {
                val imageUri = Uri.parse(imageUriString)
                val photoFile = File(imageUri.path!!)

                val compressedData = compressImageFile(photoFile)
                val photoRequestBody = compressedData.toRequestBody("image/jpeg".toMediaTypeOrNull())
                val photoPart = MultipartBody.Part.createFormData("file", photoFile.name, photoRequestBody)

                val csvRequestBody = createCsvRequestBody(csvData)
                val csvPart = MultipartBody.Part.createFormData("csv", "abacus_data.csv", csvRequestBody)

                val response = RetrofitClient.abacusPhotoService.confirmAndUpload(
                    factoryId = factoryId,
                    shiftId = shiftId,
                    takenBy = takenBy,
                    abacusId = abacusId,
                    file = photoPart,
                    csv = csvPart
                )

                if (response.isSuccessful && response.body()?.data != null) {
                    Log.d("AbacusRepository", "Confirmação enviada com sucesso!")
                    Result.success(response.body()!!.data!!)
                } else {
                    Log.e("AbacusRepository", "Falha na confirmação: ${response.message()}")
                    Result.failure(Exception("Falha na confirmação: ${response.code()}"))
                }

            } catch (e: Exception) {
                Log.e("AbacusRepository", "Exceção na confirmação", e)
                Result.failure(e)
            }
        }
    }
}

package com.mobile.app_iara.data.model.response

import com.google.gson.annotations.SerializedName

data class AbacusAnalysisResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("status")
    val status: Int,

    @SerializedName("timestamp")
    val timestamp: String,

    @SerializedName("data")
    val data: AbacusAnalysisData
)

data class AbacusAnalysisData(
    @SerializedName("arquivo")
    val arquivo: String,

    @SerializedName("resultado_csv")
    val resultadoCsv: String,

    @SerializedName("excel_download")
    val excelDownload: String,

    @SerializedName("mensagem")
    val mensagem: String
)
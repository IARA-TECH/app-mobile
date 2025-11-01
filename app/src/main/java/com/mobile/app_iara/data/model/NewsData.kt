package com.mobile.app_iara.data.model

import com.google.gson.annotations.SerializedName

data class NewsData(
    @SerializedName("titulo")
    val titulo: String,

    @SerializedName("url")
    val url: String,

    @SerializedName("imagem")
    val imagem: String,

    @SerializedName("lide")
    val lide: String?,

    @SerializedName("fonte")
    val fonte: String
)
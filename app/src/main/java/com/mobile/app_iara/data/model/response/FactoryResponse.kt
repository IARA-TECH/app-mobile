package com.mobile.app_iara.data.model.response

import com.google.gson.annotations.SerializedName

data class FactoryResponse(
    @SerializedName("name")
    val name: String,

    @SerializedName("cnpj")
    val cnpj: String,

    @SerializedName("domain")
    val domain: String,

    @SerializedName("description")
    val description: String
)
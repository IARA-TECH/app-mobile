package com.mobile.app_iara.data.model.response

import com.google.gson.annotations.SerializedName
import com.mobile.app_iara.data.model.NewsData

data class NewsResponse (
    @SerializedName("total_count")
    val totalCount: Int,

    @SerializedName("data")
    val data: List<NewsData>
)
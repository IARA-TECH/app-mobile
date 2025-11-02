package com.mobile.app_iara.data.model.request

data class LineCreateRequest(
    val name: String,
    val lineType: LineTypeCreateRequest
)
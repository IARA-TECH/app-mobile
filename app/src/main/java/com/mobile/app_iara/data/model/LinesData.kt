package com.mobile.app_iara.data.model

data class Line(
    val name: String,
    val type: LineType
)

data class LineType(
    val id: String,
    val name: String,
    val createdDate: String
)
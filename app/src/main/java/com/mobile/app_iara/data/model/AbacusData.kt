package com.mobile.app_iara.data.model

data class AbacusData(
    val id: String,
    val factoryId: Int,
    val name: String,
    val description: String,
    val lines: List<String>,
    val columns: List<ColumnData>
)

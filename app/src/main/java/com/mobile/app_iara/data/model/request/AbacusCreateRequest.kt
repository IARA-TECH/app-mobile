package com.mobile.app_iara.data.model.request

data class AbacusCreateRequest(
    val name: String,
    val description: String,
    val factoryId: Int,
    val lines: List<LineCreateRequest>,
    val columns: List<ColumnCreateRequest>
)

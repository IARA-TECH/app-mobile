package com.mobile.app_iara.ui.abacus

data class Abacus(
    val title: String,
    val description: String,
    val lines: Int,
    val columns: Int,
    val imageUrls: List<String>
)
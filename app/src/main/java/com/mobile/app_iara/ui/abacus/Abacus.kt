package com.mobile.app_iara.ui.abacus

data class Abacus(
    val id: String,
    val title: String,
    val description: String,
    val lines: Int,
    val columns: Int,
    val factoryId: Int
)
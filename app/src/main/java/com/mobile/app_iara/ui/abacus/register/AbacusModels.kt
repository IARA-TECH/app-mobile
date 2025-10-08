package com.mobile.app_iara.ui.abacus.register

data class SubColumn(
    val color: String,
    val value: Int
)

data class AbacusColumn(
    val name: String,
    val subColumns: MutableList<SubColumn>
)

data class Abacus(
    val name: String,
    val description: String,
    val lines: List<String>,
    val columns: List<AbacusColumn>
)
package com.mobile.app_iara.data.model.response

data class FarmCondemnationResponse(
    val title: String,
    val total: Int,
    val averageRate: Float,
    val previousComparison: Float,
    val reasonRanking: List<ReasonRankingItem>,
    val monthlyEvolution: FarmMonthlyEvolution
)

data class ReasonRankingItem(
    val reason: String,
    val quantity: Int
)

data class FarmMonthlyEvolution(
    val periods: List<String>,
    val values: List<Float>
)
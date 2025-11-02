package com.mobile.app_iara.data.model.response

data class DashboardComparisonResponse(
    val title: String,
    val periods: List<String>,
    val technicalFailures: List<Float>,
    val farmCondemnations: List<Float>,
    val totals: Totals,
    val monthlyRanking: List<MonthlyRanking>
)

data class Totals(
    val totalFarmCondemnations: Int,
    val totalTechnicalFailures: Int
)

data class MonthlyRanking(
    val month: String,
    val total: Int
)
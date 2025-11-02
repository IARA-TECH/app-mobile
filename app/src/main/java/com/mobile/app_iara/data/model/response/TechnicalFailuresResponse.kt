package com.mobile.app_iara.data.model.response

data class TechnicalFailuresResponse(
    val total: Int,
    val averageRate: Double,
    val previousComparison: Double,
    val monthlyEvolution: EvolutionData?,
    val ranking: List<TechnicalRankingData>?
)

data class EvolutionData(
    val periods: List<String>,
    val values: List<Float>
)

data class TechnicalRankingData(
    val name: String,
    val total: Int
)
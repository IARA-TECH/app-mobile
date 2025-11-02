package com.mobile.app_iara.data.model.response

data class ShiftComparisonResponse(
    val title: String,
    val quantityPerShift: List<QuantityPerShift>,
    val monthlyEvolution: MonthlyEvolution
)

data class QuantityPerShift(
    val shift: String,
    val quantity: Int
)

data class MonthlyEvolution(
    val periods: List<String>,
    val morning: List<Float>,
    val afternoon: List<Float>,
    val night: List<Float>
)
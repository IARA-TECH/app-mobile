package com.mobile.app_iara.util

import com.mobile.app_iara.data.model.AbacusData
import com.mobile.app_iara.ui.abacus.Abacus

object AbacusMapper {

    fun mapApiToUi(apiData: AbacusData): Abacus {
        return Abacus(
            title = apiData.name,
            description = apiData.description,
            lines = apiData.lines.size,
            columns = apiData.columns.size,
            imageUrls = emptyList()
        )
    }

    fun mapApiListToUiList(apiList: List<AbacusData>): List<Abacus> {
        return apiList.map { mapApiToUi(it) }
    }
}
package com.mobile.app_iara.util

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

object DataUtil {

    fun generateUuid(): String {
        return UUID.randomUUID().toString()
    }

    fun getCurrentIsoTimestamp(): String {
        return OffsetDateTime.now(java.time.ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

    const val TYPE_CONDENA_GRANJA_NAME = "Condena pela granja"
    const val TYPE_FALHA_TECNICA_NAME = "Falha técnica"
}
package com.mobile.app_iara.util

import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone
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

    fun formatIsoDateToAppDate(isoDate: String?): String {
        if (isoDate.isNullOrEmpty()) return "Data indisponível"

        return try {
            val apiFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            apiFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = apiFormat.parse(isoDate)

            val appFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            appFormat.format(date)
        } catch (e: Exception) {
            "Data inválida"
        }
    }
}
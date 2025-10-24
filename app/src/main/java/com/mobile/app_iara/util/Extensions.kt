package com.mobile.app_iara.util

fun String.formatCnpj(): String {
    val cleaned = this.filter { it.isDigit() }

    if (cleaned.length != 14) {
        return this
    }

    return cleaned.replace(Regex("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})"), "$1.$2.$3/$4-$5")
}

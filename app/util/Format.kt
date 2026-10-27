package com.example.novoecommerce.util

import java.text.NumberFormat
import java.util.Locale

private val brl: NumberFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))

fun Double.toBrl(): String = brl.format(this)

fun String.masked(visible: Int = 4): String {
    if (isBlank()) return ""
    return "*".repeat((length - visible).coerceAtLeast(0)) + takeLast(visible)
}

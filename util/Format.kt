package com.example.novoecommerce.util

import java.text.NumberFormat
import java.util.Locale

private val brl: NumberFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))

/** Formata como moeda brasileira, ex.: R$ 100,00 */
fun Double.toBrl(): String = brl.format(this)

/** Mostra só os últimos [visible] caracteres, o resto vira "*". */
fun String.masked(visible: Int = 4): String {
    if (isBlank()) return ""
    return "*".repeat((length - visible).coerceAtLeast(0)) + takeLast(visible)
}

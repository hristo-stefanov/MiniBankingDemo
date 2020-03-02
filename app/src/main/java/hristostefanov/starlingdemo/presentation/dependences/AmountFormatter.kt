package hristostefanov.starlingdemo.presentation.dependences

import java.math.BigDecimal
import java.util.*

interface AmountFormatter {
    fun format(amount: BigDecimal, currencyCode: String, locale: Locale): String
}
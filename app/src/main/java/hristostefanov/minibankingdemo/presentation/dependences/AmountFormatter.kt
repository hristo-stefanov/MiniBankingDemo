package hristostefanov.minibankingdemo.presentation.dependences

import java.math.BigDecimal

interface AmountFormatter {
    fun format(amount: BigDecimal, currencyCode: String): String
}
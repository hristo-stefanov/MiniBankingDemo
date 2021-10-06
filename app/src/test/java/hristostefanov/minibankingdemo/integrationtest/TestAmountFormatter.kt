package hristostefanov.minibankingdemo.integrationtest

import hristostefanov.minibankingdemo.presentation.dependences.AmountFormatter
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

/**
 * [AmountFormatter] depends on Android for ICU so we use the stock Java [NumberFormat] for testing.
 */
class TestAmountFormatter @Inject constructor(private val locale: Locale): AmountFormatter {
    override fun format(amount: BigDecimal, currencyCode: String): String {
        return NumberFormat.getCurrencyInstance(locale).apply {
            currency = Currency.getInstance(currencyCode)
        }.format(amount)
    }
}
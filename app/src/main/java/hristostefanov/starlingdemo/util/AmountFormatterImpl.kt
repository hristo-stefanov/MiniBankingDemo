package hristostefanov.starlingdemo.util

import hristostefanov.starlingdemo.presentation.dependences.AmountFormatter
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

class AmountFormatterImpl @Inject constructor():
    AmountFormatter {
    override fun format(amount: BigDecimal, currencyCode: String, locale: Locale): String {
        // Use ICU's android.icu.text.NumberFormat for much better formatting of currencies in different locales!!!
        return android.icu.text.NumberFormat.getCurrencyInstance(locale).apply {
            currency = android.icu.util.Currency.getInstance(currencyCode)
            // needed for some locales like bg_BG
            isGroupingUsed = true
        }.format(amount)
    }
}
package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.SavedStateHandle
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import java.util.*

// these constants corresponds to the fragment argument names in
// the navigation graph
const val ACCOUNT_ID_KEY = "accountId"
const val ROUND_UP_AMOUNT_KEY = "roundUpAmount"
const val ACCOUNT_CURRENCY_KEY = "accountCurrency"
const val NAME_KEY = "name"
const val SAVINGS_GOAL_KEY = "savingsGoal"

var SavedStateHandle.name: String
    get() = this[NAME_KEY] ?: throw IllegalArgumentException(NAME_KEY)
    set(value) { this[NAME_KEY] = value }

val SavedStateHandle.currency: Currency
    get() = this[ACCOUNT_CURRENCY_KEY] ?: throw IllegalArgumentException(ACCOUNT_CURRENCY_KEY)

val SavedStateHandle.accountId: String
    get() = this[ACCOUNT_ID_KEY] ?: throw IllegalArgumentException(ACCOUNT_ID_KEY)

val SavedStateHandle.roundUpAmount: BigDecimal
    get() = this[ROUND_UP_AMOUNT_KEY] ?: throw IllegalArgumentException(ROUND_UP_AMOUNT_KEY)
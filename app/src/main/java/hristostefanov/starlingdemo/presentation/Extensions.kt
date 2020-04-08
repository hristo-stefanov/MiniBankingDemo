package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.SavedStateHandle
import hristostefanov.starlingdemo.business.entities.SavingsGoal
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import java.util.*

// these keys corresponds to the fragment argument names in
// the navigation graph, so as arguments can be directly added to the [SavedStateHandle] instances
const val ACCOUNT_ID_KEY = "accountId"
const val ROUND_UP_AMOUNT_KEY = "roundUpAmount"
const val ACCOUNT_CURRENCY_KEY = "accountCurrency"
const val SAVINGS_GOAL_KEY = "savingsGoal"

var SavedStateHandle.accountCurrency: Currency
    get() = this[ACCOUNT_CURRENCY_KEY] ?: throw IllegalArgumentException(ACCOUNT_CURRENCY_KEY)
    set(value) { this[ACCOUNT_CURRENCY_KEY] = value}

var SavedStateHandle.accountId: String
    get() = this[ACCOUNT_ID_KEY] ?: throw IllegalArgumentException(ACCOUNT_ID_KEY)
    set(value) { this[ACCOUNT_ID_KEY] = value}

var SavedStateHandle.roundUpAmount: BigDecimal
    get() = this[ROUND_UP_AMOUNT_KEY] ?: throw IllegalArgumentException(ROUND_UP_AMOUNT_KEY)
    set(value) { this[ROUND_UP_AMOUNT_KEY] = value}

var SavedStateHandle.savingsGoal: SavingsGoal
    get() = this[SAVINGS_GOAL_KEY] ?: throw IllegalArgumentException(SAVINGS_GOAL_KEY)
    set(value) { this[SAVINGS_GOAL_KEY] = value}
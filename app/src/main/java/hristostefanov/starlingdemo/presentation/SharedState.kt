package hristostefanov.starlingdemo.presentation

import androidx.annotation.MainThread
import hristostefanov.starlingdemo.business.entities.SavingsGoal
import hristostefanov.starlingdemo.util.SessionScope
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

@MainThread
@SessionScope
class SharedState @Inject constructor() {
    lateinit var savingsGoal: SavingsGoal
    lateinit var accountId: String
    lateinit var accountCurreny: Currency
    lateinit var roundUpAmount: BigDecimal
    var accessToken: String = ""
    var isMockService: Boolean = false
}
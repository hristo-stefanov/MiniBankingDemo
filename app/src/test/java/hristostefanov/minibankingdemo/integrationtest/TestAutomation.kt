package hristostefanov.minibankingdemo.integrationtest

import hristostefanov.minibankingdemo.presentation.AccountsViewModel
import java.math.BigDecimal

interface TestAutomation {
    fun createAccount(number: String, currency: String, transactions: List<BigDecimal>)
    fun calculateRoundUp(accountNumber: String): BigDecimal
    fun theCalculatedRoundUpIsOne()
    fun openAccountScreen(): AccountsViewModel
}


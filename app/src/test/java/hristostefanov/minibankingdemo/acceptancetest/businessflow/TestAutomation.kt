package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.presentation.AccountsViewModel
import java.math.BigDecimal

interface TestAutomation {
    fun login()
    fun createAccount(number: String, currency: String, transactions: List<BigDecimal>)
    fun calculateRoundUp(accountNumber: String): BigDecimal
    fun theCalculatedRoundUpIsOne()
    fun openAccountScreen(): AccountsViewModel
}


package hristostefanov.minibankingdemo.integrationtest

import java.math.BigDecimal

interface TestAutomation {
    fun createAccount(number: String, currency: String, transactions: List<BigDecimal>)
    fun calculateRoundUp(accountNumber: String): BigDecimal
}


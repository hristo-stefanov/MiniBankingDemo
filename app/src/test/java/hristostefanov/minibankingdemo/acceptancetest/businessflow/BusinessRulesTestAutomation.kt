package hristostefanov.minibankingdemo.acceptancetest.businessflow

import java.math.BigDecimal

interface BusinessRulesTestAutomation {
    fun createAccount(number: String, currency: String, transactions: List<BigDecimal>)
    fun calculateRoundUp(accountNumber: String): BigDecimal
}
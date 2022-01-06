package hristostefanov.minibankingdemo.acceptancetest.businessflow

import java.math.BigDecimal

/**
 * Automation interface for exercising the business layer and for stubbing data layer responses.
 *
 * No authentication is involved.
 */
interface BusinessRulesTestAutomation {
    // stubbing
    fun createAccount(number: String, currency: String, transactions: List<BigDecimal>)

    // exercising
    fun calculateRoundUp(accountNumber: String): BigDecimal
}
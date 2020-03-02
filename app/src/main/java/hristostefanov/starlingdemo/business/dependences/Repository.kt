package hristostefanov.starlingdemo.business.dependences

import hristostefanov.starlingdemo.business.entities.Account
import hristostefanov.starlingdemo.business.entities.SavingsGoal
import hristostefanov.starlingdemo.business.entities.Transaction
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

interface Repository {

    @Throws(ServiceException::class)
    suspend fun findAllAccounts(): List<Account>

    @Throws(ServiceException::class)
    suspend fun findTransactions(
        accountId: String,
        sinceDate: LocalDate,
        zoneId: ZoneId
    ): List<Transaction>

    @Throws(ServiceException::class)
    suspend fun findSavingGoals(accountId: String): List<SavingsGoal>

    @Throws(ServiceException::class)
    suspend fun createSavingsGoal(name: String, accountId: String, currency: String)

    @Throws(ServiceException::class)
    suspend fun addMoneyIntoSavingsGoal(
        accountId: String,
        savingsGoalId: String,
        currency: Currency,
        amount: BigDecimal,
        transferId: UUID
    )
}
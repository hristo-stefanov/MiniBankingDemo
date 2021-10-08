package hristostefanov.minibankingdemo.acceptancetest.technical

import hristostefanov.minibankingdemo.acceptancetest.businessflow.BusinessRulesTestAutomation
import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.entities.*
import hristostefanov.minibankingdemo.business.interactors.CalcRoundUpInteractor
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import javax.inject.Inject

class BusinessRulesTestAutomationImpl @Inject constructor(
    private val zoneId: ZoneId,
): BusinessRulesTestAutomation {

    private lateinit var repository: Repository

    private val calcRoundUpInteractor by lazy {
        CalcRoundUpInteractor(repository, zoneId)
    }

    override fun calculateRoundUp(accountNumber: String): BigDecimal {
        // TODO pass test dispatcher or make this function suspend
        return runBlocking {
            calcRoundUpInteractor.execute(accountNumber, LocalDate.now())
        }
    }

    override fun createAccount(number: String, currency: String, transactions: List<BigDecimal>) {
        repository = object : Repository {
            override suspend fun findAllAccounts(): List<Account> {
                return listOf(Account(number, number, "", Currency.getInstance(currency), "0.00".toBigDecimal()))
            }

            override suspend fun findTransactions(
                accountId: String,
                since: ZonedDateTime
            ): List<Transaction> {
                return transactions.map { amount ->
                    Transaction(amount, Status.SETTLED, Source.EXTERNAL)
                }
            }

            override suspend fun findSavingGoals(accountId: String): List<SavingsGoal> {
                throw AssertionError()
            }

            override suspend fun createSavingsGoal(
                name: String,
                accountId: String,
                currency: Currency
            ) {
                throw AssertionError()
            }

            override suspend fun addMoneyIntoSavingsGoal(
                accountId: String,
                savingsGoalId: String,
                currency: Currency,
                amount: BigDecimal,
                transferId: UUID
            ) {
                throw AssertionError()
            }
        }
    }
}
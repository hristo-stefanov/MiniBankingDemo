package hristostefanov.minibankingdemo.business.interactors.shared

import hristostefanov.minibankingdemo.business.calcRoundup
import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.entities.Source
import hristostefanov.minibankingdemo.business.entities.Status
import hristostefanov.minibankingdemo.business.interactors.CalcRoundUpInteractor
import hristostefanov.minibankingdemo.business.isTransactionEligibleForRoundup
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZoneId

class ShowAccountsAndRoundupsInteractor constructor(
    private val zoneId: ZoneId,
    private val repository: Repository,
    val output: ShowAccountsAndRoundupsOutputBoundary
) {
    suspend fun execute() {
        val roundUpSinceDate: LocalDate = LocalDate.now().minusWeeks(1)
        val zonedDateTime = roundUpSinceDate.atStartOfDay(zoneId)

        val reportItems = repository.findAllAccounts().map { account ->
            val transactions = repository.findTransactions(account.id, zonedDateTime)
            val eligibleTransactions = transactions.filter { isTransactionEligibleForRoundup(it) }
            val roundUp = calcRoundup(eligibleTransactions.map { it.amount} )

            AccountsAndRoundupsModel.Item(
                accountId =  account.id,
                number = account.accountNum,
                balance = account.balance,
                roundUp = roundUp,
            )
        }


        val model = AccountsAndRoundupsModel(reportItems)
        output.showReport(model)
    }
}
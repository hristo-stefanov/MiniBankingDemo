package hristostefanov.minibankingdemo.business.interactors.shared

import hristostefanov.minibankingdemo.business.calcRoundup
import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.isSpendingTransaction
import java.time.LocalDate
import java.time.ZoneId

class PresentAccountsAndRoundupsInteractor constructor(
    private val zoneId: ZoneId,
    private val repository: Repository,
    val output: PresentAccountsAndRoundUpsOutputBoundary
) {
    suspend fun execute() {
        val roundUpSinceDate: LocalDate = LocalDate.now().minusWeeks(1)
        val zonedDateTime = roundUpSinceDate.atStartOfDay(zoneId)

        val reportItems = repository.findAllAccounts().map { account ->
            val transactions = repository.findTransactions(account.id, zonedDateTime)
            val eligibleTransactions = transactions.filter { isSpendingTransaction(it) }
            val roundUp = calcRoundup(eligibleTransactions.map { it.amount} )

            AccountsAndRoundUpsModel.Item(
                accountId =  account.id,
                number = account.accountNum,
                balance = account.balance,
                roundUp = roundUp,
            )
        }


        val model = AccountsAndRoundUpsModel(reportItems)
        output.present(model)
    }
}
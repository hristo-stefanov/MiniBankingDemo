package hristostefanov.minibankingdemo.business.interactors.shared

import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.entities.Source
import hristostefanov.minibankingdemo.business.entities.Status
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

            val settledPaymentsAmounts = transactions
                .filter { it.amount.signum() == -1 && it.status == Status.SETTLED && it.source == Source.EXTERNAL }
                .map { it.amount.negate() }

            val roundUp = settledPaymentsAmounts
                // get the fractional part
                .map { it.remainder(BigDecimal.ONE) }
                // consider only greater than zero fractional parts (zero's complement to 1 is 1)
                .filter { it.signum() == 1 }
                // get the complement to 1
                .map { BigDecimal.ONE.minus(it) }
                // accumulate the complements
                // NOTE unlike #reduce, #fold allows empty collection by getting the initial value
                // as argument instead of using the first element of the collection
                .fold(BigDecimal.ZERO) { acc, item -> acc.add(item) }

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
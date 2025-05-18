package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.business.calcAccountRoundUp
import hristostefanov.minibankingdemo.business.calcStartOfSevenDayWindowIncludingToday
import hristostefanov.minibankingdemo.business.calcTransactionRoundUp
import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.entities.Transaction
import hristostefanov.minibankingdemo.business.isSpendingTransaction
import java.math.BigDecimal
import java.time.OffsetDateTime

class CalcAccountRoundUpInteractor(
    private val repository: Repository,
    private val calcTransactionRoundUpPolicy: (Transaction) -> BigDecimal = ::calcTransactionRoundUp,
    private val isSpendingTransactionPolicy: (Transaction) -> Boolean = ::isSpendingTransaction,
    private val calcSincePolicy: (OffsetDateTime) -> OffsetDateTime = ::calcStartOfSevenDayWindowIncludingToday
) {
    suspend operator fun invoke(
        accountId: String,
        now: OffsetDateTime,
    ): BigDecimal {
        val since = calcSincePolicy(now)
        val transactions = repository.findTransactions(accountId, since)
        return calcAccountRoundUp(transactions, calcTransactionRoundUpPolicy, isSpendingTransactionPolicy)
    }
}
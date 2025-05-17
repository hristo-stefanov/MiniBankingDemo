package hristostefanov.minibankingdemo.business

import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.entities.Source
import hristostefanov.minibankingdemo.business.entities.Status
import hristostefanov.minibankingdemo.business.entities.Transaction
import org.jetbrains.annotations.Contract
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.math.RoundingMode
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

@Contract(pure = true)
fun calcStartOfSevenDayWindowIncludingToday(now: OffsetDateTime): OffsetDateTime =
    now.truncatedTo(ChronoUnit.DAYS).minus(6, ChronoUnit.DAYS)

@Contract(pure = true)
fun isSpendingTransaction(transaction: Transaction) =
    transaction.amount.signum() == -1
            && transaction.status == Status.SETTLED
            && transaction.source == Source.EXTERNAL


@Contract(pure = true)
fun calcTransactionRoundUp(transaction: Transaction): BigDecimal =
    transaction.amount.setScale(0, RoundingMode.CEILING).minus(transaction.amount)

/**
 * The is eligibility criteria for transactions is covered by [isSpendingTransaction] and
 * by the combination of [Repository.findAllAccounts] plus [since]
 */
suspend fun Repository.calcAccountRoundUpInteractor(
    accountId: String,
    now: OffsetDateTime,
    calcTransactionRoundUpPolicy: (Transaction) -> BigDecimal = ::calcTransactionRoundUp,
    isSpendingTransactionPolicy: (Transaction) -> Boolean = ::isSpendingTransaction,
    calcSincePolicy: (OffsetDateTime) -> OffsetDateTime = ::calcStartOfSevenDayWindowIncludingToday
): BigDecimal {
    val since = calcSincePolicy(now)
    val transactions = findTransactions(accountId, since)
    return calcAccountRoundUp(transactions, calcTransactionRoundUpPolicy, isSpendingTransactionPolicy)
}

private fun calcAccountRoundUp(
    transactions: List<Transaction>,
    calcTransactionRoundUpPolicy: (Transaction) -> BigDecimal = ::calcTransactionRoundUp,
    isSpendingTransactionPolicy: (Transaction) -> Boolean = ::isSpendingTransaction
): BigDecimal = transactions
    .filter { isSpendingTransactionPolicy(it) }
    .map { calcTransactionRoundUpPolicy(it) }
    .fold(ZERO, BigDecimal::add)
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
private fun calcRoundUp(transaction: Transaction): BigDecimal =
    transaction.amount.setScale(0, RoundingMode.CEILING).minus(transaction.amount)

/**
 * The is eligibility criteria for transactions is covered by [isSpendingTransaction] and
 * by the combination of [Repository.findAllAccounts] plus [since]
 */
suspend fun calcAccountRoundUp(
    repository: Repository,
    accountId: String,
    since: OffsetDateTime,
    calcTransactionRoundUpPolicy: (Transaction) -> BigDecimal = ::calcRoundUp,
    isSpendingTransactionPolicy: (Transaction) -> Boolean = ::isSpendingTransaction
): BigDecimal {
    val transactions = repository.findTransactions(accountId, since)
    return transactions
        .filter { isSpendingTransactionPolicy(it) }
        .map { calcTransactionRoundUpPolicy(it) }
        .fold(ZERO, BigDecimal::add)
}

/**
 * Implements this FEEL expression: sum(for t in transactions return ceiling(t) - t)
 */
@Contract(pure = true)
fun calcRoundup(amounts: List<BigDecimal>): BigDecimal = amounts
    .map { it.setScale(0, RoundingMode.CEILING).minus(it) }
    .fold(BigDecimal.ZERO, BigDecimal::add)

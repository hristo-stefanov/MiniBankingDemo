package hristostefanov.minibankingdemo.business

import hristostefanov.minibankingdemo.business.entities.Source
import hristostefanov.minibankingdemo.business.entities.Status
import hristostefanov.minibankingdemo.business.entities.Transaction
import org.jetbrains.annotations.Contract
import java.math.BigDecimal
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
fun calcRoundUp(amount: BigDecimal): BigDecimal =
    amount.setScale(0, RoundingMode.CEILING).minus(amount)


// TODO add filtering on time
@Contract(pure = true)
fun isEligible(transaction: Transaction) = isSpendingTransaction(transaction)

fun calcAccountRoundUp(
    transactions: List<Transaction>,
    isEligiblePolicy: (Transaction) -> Boolean
) = transactions
    .filter { isEligiblePolicy(it) }
    .map { calcRoundUp(it.amount) }
    .fold(BigDecimal.ZERO, BigDecimal::add)

/**
 * Implements this FEEL expression: sum(for t in transactions return ceiling(t) - t)
 */
@Contract(pure = true)
fun calcRoundup(amounts: List<BigDecimal>): BigDecimal = amounts
    .map { it.setScale(0, RoundingMode.CEILING).minus(it) }
    .fold(BigDecimal.ZERO, BigDecimal::add)

package hristostefanov.minibankingdemo.business

import hristostefanov.minibankingdemo.business.entities.Source
import hristostefanov.minibankingdemo.business.entities.Status
import hristostefanov.minibankingdemo.business.entities.Transaction
import org.jetbrains.annotations.Contract
import java.math.BigDecimal
import java.math.RoundingMode

@Contract(pure = true)
fun isSpendingTransaction(transaction: Transaction) =
    transaction.amount.signum() == -1
            && transaction.status == Status.SETTLED
            && transaction.source == Source.EXTERNAL

/**
 * Implements this FEEL expression: sum(for t in transactions return ceiling(t) - t)
 */
@Contract(pure = true)
fun calcRoundup(amounts: List<BigDecimal>): BigDecimal = amounts
    .map { it.setScale(0, RoundingMode.CEILING).minus(it) }
    .fold(BigDecimal.ZERO, BigDecimal::add)

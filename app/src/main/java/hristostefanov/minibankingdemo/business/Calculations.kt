package hristostefanov.minibankingdemo.business

import hristostefanov.minibankingdemo.business.entities.Source
import hristostefanov.minibankingdemo.business.entities.Status
import hristostefanov.minibankingdemo.business.entities.Transaction
import java.math.BigDecimal

fun isTransactionEligibleForRoundup(transaction: Transaction) =
    transaction.amount.signum() == -1
            && transaction.status == Status.SETTLED
            && transaction.source == Source.EXTERNAL

fun calcRoundup(amounts: List<BigDecimal>): BigDecimal = amounts
    // TODO optimize processing
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

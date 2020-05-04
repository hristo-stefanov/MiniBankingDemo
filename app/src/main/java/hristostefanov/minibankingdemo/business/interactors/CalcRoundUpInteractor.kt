package hristostefanov.minibankingdemo.business.interactors

import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.dependences.ServiceException
import hristostefanov.minibankingdemo.business.entities.Source
import hristostefanov.minibankingdemo.business.entities.Status
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class CalcRoundUpInteractor @Inject constructor(
    private val repository: Repository,
    private val zoneId: ZoneId
) {
    @Throws(ServiceException::class)
    suspend fun execute(accountId: String, sinceDate: LocalDate): BigDecimal {
        val zonedDateTime = sinceDate.atStartOfDay(zoneId)
        val transactions = repository.findTransactions(accountId, zonedDateTime)

        val settledPaymentsAmounts = transactions
            .filter { it.amount.signum() == -1 && it.status == Status.SETTLED && it.source == Source.EXTERNAL }
            .map { it.amount.negate() }

        return settledPaymentsAmounts
            // get the fractional part
            .map { it.remainder(BigDecimal.ONE) }
            // consider only greater than zero factional parts
            .filter { it.compareTo(BigDecimal.ZERO) == 1 }
            // get the complement to 1
            .map { BigDecimal.ONE.minus(it) }
            // accumulate the complements
            // NOTE unlike #reduce, #fold allows empty collection by getting the initial value
            // as argument instead of using the first element of the collection
            .fold(BigDecimal.ZERO) { acc, bigDecimal -> acc.add(bigDecimal) }
    }
}
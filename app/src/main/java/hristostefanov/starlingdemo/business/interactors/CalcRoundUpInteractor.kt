package hristostefanov.starlingdemo.business.interactors

import hristostefanov.starlingdemo.business.dependences.Repository
import hristostefanov.starlingdemo.business.dependences.ServiceException
import hristostefanov.starlingdemo.business.entities.Source
import hristostefanov.starlingdemo.business.entities.Status
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class CalcRoundUpInteractor @Inject constructor(private val _repository: Repository) {
    @Throws(ServiceException::class)
    suspend fun execute(accountId: String, sinceDate: LocalDate, zoneId: ZoneId): BigDecimal {
        val transactions = _repository.findTransactions(accountId, sinceDate, zoneId)

        val settledPaymentsAmounts = transactions
            .filter { it.amount.signum() == -1 && it.status == Status.SETTLED && it.source == Source.EXTERNAL }
            .map { it.amount.negate() }

        return settledPaymentsAmounts
            .map { it.remainder(BigDecimal.ONE) }
            .filter { it.compareTo(BigDecimal.ZERO) == 1 }
            .map { BigDecimal.ONE.minus(it) }
            .fold(BigDecimal.ZERO) { acc, bigDecimal -> acc.add(bigDecimal) }
    }
}
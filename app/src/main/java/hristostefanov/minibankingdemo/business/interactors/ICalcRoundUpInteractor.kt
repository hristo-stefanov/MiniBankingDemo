package hristostefanov.minibankingdemo.business.interactors

import hristostefanov.minibankingdemo.business.dependences.ServiceException
import java.math.BigDecimal
import java.time.LocalDate

interface ICalcRoundUpInteractor {
    @Throws(ServiceException::class)
    suspend fun execute(accountId: String, sinceDate: LocalDate): BigDecimal
}
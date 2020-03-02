package hristostefanov.starlingdemo.business.interactors

import hristostefanov.starlingdemo.business.dependences.Repository
import hristostefanov.starlingdemo.business.dependences.ServiceException
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

class AddMoneyIntoGoalInteractor @Inject constructor(private val _repository: Repository) {
    // for idempotency, is cause of executed more than once
    private val transferId = UUID.randomUUID()

    @Throws(ServiceException::class)
    suspend fun execute(
        accountId: String,
        savingsGoalId: String,
        currency: Currency,
        amount: BigDecimal
    ) {
        _repository.addMoneyIntoSavingsGoal(accountId, savingsGoalId, currency, amount, transferId)
    }
}
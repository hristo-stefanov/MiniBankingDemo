package hristostefanov.minibankingdemo.business.interactors

import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.dependences.ServiceException
import org.greenrobot.eventbus.EventBus
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

class AddMoneyIntoGoalInteractor @Inject constructor(private val repository: Repository, private val eventBus: EventBus) {
    // for idempotency, in case this is executed more than once
    private val transferId = UUID.randomUUID()

    @Throws(ServiceException::class)
    suspend fun execute(
        accountId: String,
        savingsGoalId: String,
        currency: Currency,
        amount: BigDecimal
    ) {
        repository.addMoneyIntoSavingsGoal(accountId, savingsGoalId, currency, amount, transferId)
        eventBus.post(DataSourceChangedEvent())
    }
}
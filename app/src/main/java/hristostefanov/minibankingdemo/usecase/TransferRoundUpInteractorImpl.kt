package hristostefanov.minibankingdemo.usecase

import arrow.core.Either
import arrow.core.recover
import hristostefanov.minibankingdemo.util.DataSourceChangedEvent
import hristostefanov.minibankingdemo.usecase.input.Completion
import hristostefanov.minibankingdemo.usecase.input.Termination.Failure
import hristostefanov.minibankingdemo.usecase.input.TransferRoundUpInteractor
import hristostefanov.minibankingdemo.usecase.input.Status
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import org.greenrobot.eventbus.EventBus
import java.math.BigDecimal
import java.util.Currency
import java.util.UUID
import javax.inject.Inject

class TransferRoundUpInteractorImpl @Inject constructor(
    private val loginSessionRegistry: LoginSessionRegistry,
    private val eventBus: EventBus
) : TransferRoundUpInteractor {
    override suspend fun invoke(
        accountId: String,
        accountCurrency: Currency,
        roundUpAmount: BigDecimal,
        savingsGoalId: String
    ): Status {
        return  Either.catch {
            val transferId = UUID.randomUUID()
            loginSessionRegistry.requireComponent.repository.addMoneyIntoSavingsGoal(accountId, savingsGoalId, accountCurrency, roundUpAmount, transferId)
            eventBus.post(DataSourceChangedEvent())

            Completion
        }.recover { exception ->
            raise(Failure(exception))
        }
    }
}
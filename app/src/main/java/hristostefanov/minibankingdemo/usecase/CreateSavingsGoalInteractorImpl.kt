package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.business.dependences.ServiceException
import hristostefanov.minibankingdemo.business.interactors.DataSourceChangedEvent
import hristostefanov.minibankingdemo.usecase.input.CreateSavingsGoalInteractor
import hristostefanov.minibankingdemo.usecase.input.Outcome
import hristostefanov.minibankingdemo.usecase.output.StockUI
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import org.greenrobot.eventbus.EventBus
import java.util.Currency
import javax.inject.Inject

class CreateSavingsGoalInteractorImpl @Inject constructor(
    private val loginSessionRegistry: LoginSessionRegistry,
    private val eventBus: EventBus,
    private val stockUI: StockUI
) : CreateSavingsGoalInteractor {

    override suspend fun start(goalName: String, accountId: String, accountCurrency: Currency): Outcome {
        if (!validateName(goalName))
            throw IllegalArgumentException()

        try {
            loginSessionRegistry.component!!.repository.createSavingsGoal(
                goalName,
                accountId,
                accountCurrency
            )
            // TODO
            eventBus.post(DataSourceChangedEvent())

            return Outcome.Completed(Unit)
        } catch (e: ServiceException) {
            e.localizedMessage?.let { stockUI.presentMessage(it) }
            return Outcome.Failed(e)
        }
    }

    // TODO it's also in the view model
    private fun validateName(name: String) = name.isNotBlank()
}


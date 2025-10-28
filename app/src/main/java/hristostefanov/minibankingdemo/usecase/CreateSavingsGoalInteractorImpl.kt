package hristostefanov.minibankingdemo.usecase

import arrow.core.Either
import arrow.core.recover
import hristostefanov.minibankingdemo.business.interactors.DataSourceChangedEvent
import hristostefanov.minibankingdemo.usecase.input.CreateSavingsGoalInteractor
import hristostefanov.minibankingdemo.usecase.input.Failure
import hristostefanov.minibankingdemo.usecase.input.Status
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import org.greenrobot.eventbus.EventBus
import java.util.Currency
import javax.inject.Inject

class CreateSavingsGoalInteractorImpl @Inject constructor(
    private val loginSessionRegistry: LoginSessionRegistry,
    private val eventBus: EventBus,
) : CreateSavingsGoalInteractor {

    override suspend fun invoke(goalName: String, accountId: String, accountCurrency: Currency): Status {
        // TODO if that's a precondition we should not validate it, right?
        if (!validateName(goalName))
            throw IllegalArgumentException()

        return  Either.catch {
            loginSessionRegistry.component!!.repository.createSavingsGoal(
                goalName,
                accountId,
                accountCurrency
            )
            // TODO
            eventBus.post(DataSourceChangedEvent())
        }.recover { exception ->
            // TODO think of more readable way to convert a Throwable into Failure
            raise(
                Failure(exception)
            )
        }
    }

    // TODO it's also in the view model
    private fun validateName(name: String) = name.isNotBlank()
}


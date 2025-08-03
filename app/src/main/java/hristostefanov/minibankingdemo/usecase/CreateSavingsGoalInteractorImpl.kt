package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.dependences.ServiceException
import hristostefanov.minibankingdemo.business.interactors.DataSourceChangedEvent
import hristostefanov.minibankingdemo.presentation.Navigation
import hristostefanov.minibankingdemo.usecase.input.TransferRoundUpInteractor
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import hristostefanov.minibankingdemo.util.NavigationChannel
import kotlinx.coroutines.channels.Channel
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class CreateSavingsGoalInteractorImpl @Inject constructor(
    private val lifecycle: InteractorLifecycleImpl,
    @NavigationChannel
    private val transferRoundupInteractor: TransferRoundUpInteractor,
    private val repository: Repository,
    private val eventBus: EventBus
): CreateSavingsGoalInteractor, InteractorLifecycle by lifecycle {

    override suspend fun start(userInterface: UserInterface) {
        userInterface.promptUserToSubmitGoalName(ContinuationId.CreateSavingsGoal_NameSubmitted)
    }

    override suspend fun onGoalNameSubmit(goalName: String, userInterface: UserInterface) {
        if (!validateName(goalName))
            throw IllegalArgumentException()

        try {
            // Query context from Transfer Round-up
            val accountId = transferRoundupInteractor.accountId
            val currency = transferRoundupInteractor.accountCurrency

            repository.createSavingsGoal(goalName, accountId, currency)

            userInterface.closeCreateSavingsGoalUI()

            eventBus.post(DataSourceChangedEvent())
        } catch(e: ServiceException) {
            e.localizedMessage?.let { userInterface.presentMessage(it) }
        }
    }

    // TODO it's also in the view model
    private fun validateName(name: String) = name.isNotBlank()
}
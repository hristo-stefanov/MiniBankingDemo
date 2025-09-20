package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.dependences.ServiceException
import hristostefanov.minibankingdemo.business.interactors.DataSourceChangedEvent
import hristostefanov.minibankingdemo.usecase.input.TransferRoundUpInteractor
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import org.greenrobot.eventbus.EventBus

class CreateSavingsGoalInteractorImpl constructor(
    private val lifecycle: InteractorLifecycleImpl,
    private val transferRoundupInteractor: TransferRoundUpInteractor,
    private val repository: Repository,
    private val eventBus: EventBus
): CreateSavingsGoalInteractor, InteractorLifecycle by lifecycle {

    override suspend fun start(userInterface: UserInterface) {
        if (lifecycle.status == InteractorStatus.Started)
            throw IllegalStateException()
        lifecycle.setStatus(InteractorStatus.Started)
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
            lifecycle.setStatus(InteractorStatus.Completed)
        } catch(e: ServiceException) {
            e.localizedMessage?.let { userInterface.presentMessage(it) }
            lifecycle.setStatus(InteractorStatus.Failed)
        }
    }

    // TODO it's also in the view model
    private fun validateName(name: String) = name.isNotBlank()

    override suspend fun cancel() {
        lifecycle.setStatus(InteractorStatus.Cancelled)
    }
}


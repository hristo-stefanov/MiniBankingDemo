package hristostefanov.minibankingdemo.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.R
import hristostefanov.minibankingdemo.ui.LOG_INTERACTORS_TAG
import hristostefanov.minibankingdemo.usecase.CancelCreateSavingsGoal
import hristostefanov.minibankingdemo.usecase.CancelTransferRoundUp
import hristostefanov.minibankingdemo.usecase.Continuation
import hristostefanov.minibankingdemo.usecase.ContinuationId
import hristostefanov.minibankingdemo.usecase.CreateSavingsGoal
import hristostefanov.minibankingdemo.usecase.CreateSavingsGoalInteractorImpl
import hristostefanov.minibankingdemo.usecase.InteractorLifecycleImpl
import hristostefanov.minibankingdemo.usecase.TransferFromAccount
import hristostefanov.minibankingdemo.usecase.TransferRoundUpInteractorImpl
import hristostefanov.minibankingdemo.usecase.Trigger
import hristostefanov.minibankingdemo.util.ContinuationChannel
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import hristostefanov.minibankingdemo.util.NavigationChannel
import hristostefanov.minibankingdemo.util.StringSupplier
import hristostefanov.minibankingdemo.util.TriggerChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val stringSupplier: StringSupplier,
    val userInterface: UserInterfaceImpl,
    private val eventBus: EventBus,
    @ContinuationChannel
    private val continuationChannel: Channel<Continuation>,
    @TriggerChannel
    private val triggerChannel: Channel<Trigger>,
    @NavigationChannel
    private val navigationChannel: Channel<Navigation>,
    private val loginSessionRegistry: LoginSessionRegistry,
) : ViewModel() {

    private val transferRoundUpInteractor = TransferRoundUpInteractorImpl(
        loginSessionRegistry,
        InteractorLifecycleImpl(),
        stringSupplier
    )

    private val createSavingsGoalInteractor = CreateSavingsGoalInteractorImpl(
        InteractorLifecycleImpl(),
        transferRoundUpInteractor,
        loginSessionRegistry,
        eventBus
    )

    init {
        setUpTrackingInteractorStateChanges()

        continuationChannel.receiveAsFlow().onEach {
            executeContinuation(it.id, *it.params.toTypedArray())
        }.launchIn(viewModelScope)

        triggerChannel.receiveAsFlow().onEach {
            executeTrigger(it)
        }.launchIn(viewModelScope)
    }

    private fun setUpTrackingInteractorStateChanges() {
        transferRoundUpInteractor.statusChanged
            .onEach { status ->
                Log.d(LOG_INTERACTORS_TAG, "TransferRoundUpInteractor.status = $status")
                // TODO what statuses we need here?
                if (status.isFinished()) {
                    navigationChannel.send(Navigation.Before(R.id.savingsGoalsDestination))
                }
            }
            .launchIn(viewModelScope)

        createSavingsGoalInteractor.statusChanged
            .onEach { status ->
                Log.d(LOG_INTERACTORS_TAG, "CreateSavingsGoalInteractor.status = $status")
                // TODO what statuses we need here?
                if (status.isFinished()) {
                    navigationChannel.send(Navigation.Backward)
                }
            }
            .launchIn(viewModelScope)
    }

    suspend fun executeContinuation(continuationId: ContinuationId, vararg params: Any) {
        Log.d(LOG_INTERACTORS_TAG, "executeContinuation: continuationId = $continuationId param = $params")

        when (continuationId) {
            ContinuationId.TransferRoundUp_SavingsGoalSelected ->
                transferRoundUpInteractor.onSavingsGaolSelected(params[0] as String, params[1] as String,  userInterface)

            ContinuationId.TransferRoundUp_Confirmed ->
                transferRoundUpInteractor.onTransferConfirmed(userInterface)

            ContinuationId.CreateSavingsGoal_NameSubmitted ->
                createSavingsGoalInteractor.onGoalNameSubmit(params[0] as String, userInterface)
        }
    }

    private suspend fun executeTrigger(trigger: Trigger) {
        when (trigger) {
            is TransferFromAccount -> transferRoundUpInteractor.start(
                trigger.accountId,
                trigger.currency,
                trigger.roundUpAmount,
                userInterface
            )

            CreateSavingsGoal -> createSavingsGoalInteractor.start(userInterface)
            CancelTransferRoundUp -> transferRoundUpInteractor.cancel()
            CancelCreateSavingsGoal -> createSavingsGoalInteractor.cancel(userInterface)
        }
    }
}

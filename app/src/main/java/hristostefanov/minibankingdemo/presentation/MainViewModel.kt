package hristostefanov.minibankingdemo.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.ui.LOG_INTERACTORS_TAG
import hristostefanov.minibankingdemo.usecase.Continuation
import hristostefanov.minibankingdemo.usecase.ContinuationId
import hristostefanov.minibankingdemo.usecase.CreateSavingsGoal
import hristostefanov.minibankingdemo.usecase.CreateSavingsGoalInteractorImpl
import hristostefanov.minibankingdemo.usecase.InteractorLifecycleImpl
import hristostefanov.minibankingdemo.usecase.TransferFromAccount
import hristostefanov.minibankingdemo.usecase.TransferRoundUpInteractorImpl
import hristostefanov.minibankingdemo.usecase.Trigger
import hristostefanov.minibankingdemo.usecase.input.EnsureLoginCredentialsInteractor
import hristostefanov.minibankingdemo.usecase.input.GetSummaryInteractor
import hristostefanov.minibankingdemo.util.ContinuationChannel
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import hristostefanov.minibankingdemo.util.StringSupplier
import hristostefanov.minibankingdemo.util.TriggerChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

private const val IS_FRESH_START_KEY = "isFreshStart"

private const val IS_TRANSFER_ROUND_UP_INTERACTOR_ACTIVE_KEY = "isTransferRoundUpInteractorActive"
private const val IS_CREATE_SAVINGS_GOAL_INTERACTOR_ACTIVE_KEY = "isCreateSavingsGoalInteractorActive"
private const val IS_ENSURE_LOGIN_CREDENTIALS_INTERACTOR_ACTIVE_KEY = "isStartupInteractorActive"
private const val IS_GET_SUMMARY_INTERACTOR_ACTIVE_KEY = "isPresentSummaryInteractorActive"


@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val stringSupplier: StringSupplier,
    private val ensureLoginCredentialsInteractor: EnsureLoginCredentialsInteractor,
    private val userInterface: UserInterfaceImpl,
    private val eventBus: EventBus,
    @ContinuationChannel
    private val continuationChannel: Channel<Continuation>,
    @TriggerChannel
    private val triggerChannel: Channel<Trigger>,
    private val getSummaryInteractor: GetSummaryInteractor,
    private val loginSessionRegistry: LoginSessionRegistry,
) : ViewModel() {

    private val transferRoundUpInteractor = TransferRoundUpInteractorImpl(
        savedStateHandle,
        repository = loginSessionRegistry.component?.repository!!,
        InteractorLifecycleImpl(),
        loginSessionRegistry.component?.addMoneyIntoGoalInteractor!!,
        stringSupplier
    )

    private val createSavingsGoalInteractor = CreateSavingsGoalInteractorImpl(
        InteractorLifecycleImpl(),
        transferRoundUpInteractor,
        loginSessionRegistry.component?.repository!!,
        eventBus
    )

    init {
        val isFreshStart: Boolean = savedStateHandle.get<Boolean?>(IS_FRESH_START_KEY) == null
        Log.d(LOG_INTERACTORS_TAG, "isFreshStart = $isFreshStart")

        savedStateHandle[IS_FRESH_START_KEY] = false

        val isEnsureLoginCredentialsInteractorActive: Boolean =
            savedStateHandle[IS_ENSURE_LOGIN_CREDENTIALS_INTERACTOR_ACTIVE_KEY] ?: false
        Log.d(LOG_INTERACTORS_TAG, "Init: isStartupInteractorActive = $isEnsureLoginCredentialsInteractorActive")

        val isGetSummaryInteractorActive: Boolean =
            savedStateHandle[IS_GET_SUMMARY_INTERACTOR_ACTIVE_KEY] ?: false
        Log.d(LOG_INTERACTORS_TAG, "Init: isPresentSummaryInteractorActive = $isGetSummaryInteractorActive")

        val isTransferRoundUpInteractorActive = savedStateHandle[IS_TRANSFER_ROUND_UP_INTERACTOR_ACTIVE_KEY] ?: false
        Log.d(LOG_INTERACTORS_TAG, "Init: isTransferRoundUpInteractorActive = $isTransferRoundUpInteractorActive")

        val isCreateSavingsGoalInteractorActive = savedStateHandle[IS_CREATE_SAVINGS_GOAL_INTERACTOR_ACTIVE_KEY] ?: false
        Log.d(LOG_INTERACTORS_TAG, "Init: isCreateSavingsGoalInteractorActive = $isCreateSavingsGoalInteractorActive")

        // Should be exactly here - after getting the saved values and before
        // starting interactors.
        keepSavingFlagsForActiveInteractors()

        continuationChannel.receiveAsFlow().onEach {
            executeContinuation(it.id, *it.params.toTypedArray())
        }.launchIn(viewModelScope)

        triggerChannel.receiveAsFlow().onEach {
            executeTrigger(it)
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            if (isEnsureLoginCredentialsInteractorActive) {
                ensureLoginCredentialsInteractor.resume()
            }

            if (isGetSummaryInteractorActive) {
                getSummaryInteractor.resume()
            }

            if (isTransferRoundUpInteractorActive) {
                transferRoundUpInteractor.resume()
            }

            if (isCreateSavingsGoalInteractorActive) {
                createSavingsGoalInteractor.resume()
            }

            if (isFreshStart) {
                getSummaryInteractor.start(userInterface)
            }
        }
    }



    private fun keepSavingFlagsForActiveInteractors() {
        ensureLoginCredentialsInteractor.statusChanged
            .onEach {
                Log.d(LOG_INTERACTORS_TAG, "EnsureLoginCredentials.status = $it")
                savedStateHandle[IS_ENSURE_LOGIN_CREDENTIALS_INTERACTOR_ACTIVE_KEY] = it.isActive()
            }
            .launchIn(viewModelScope)

        getSummaryInteractor.statusChanged
            .onEach {
                Log.d(LOG_INTERACTORS_TAG, "GetSummaryInteractor.status = $it")
                savedStateHandle[IS_GET_SUMMARY_INTERACTOR_ACTIVE_KEY] = it.isActive()
            }
            .launchIn(viewModelScope)

        transferRoundUpInteractor.statusChanged
            .onEach {
                Log.d(LOG_INTERACTORS_TAG, "TransferRoundUpInteractor.status = $it")
                savedStateHandle[IS_TRANSFER_ROUND_UP_INTERACTOR_ACTIVE_KEY] = it.isActive()
            }
            .launchIn(viewModelScope)

        createSavingsGoalInteractor.statusChanged
            .onEach {
                Log.d(LOG_INTERACTORS_TAG, "CreateSavingsGoalInteractor.status = $it")
                savedStateHandle[IS_CREATE_SAVINGS_GOAL_INTERACTOR_ACTIVE_KEY] = it.isActive()
            }
            .launchIn(viewModelScope)
    }

    suspend fun executeContinuation(continuationId: ContinuationId, vararg params: Any) {
        Log.d(LOG_INTERACTORS_TAG, "executeContinuation: continuationId = $continuationId param = $params")

        when (continuationId) {
            ContinuationId.Startup_LoginCredentialsSubmit -> ensureLoginCredentialsInteractor.onLoginCredentialsSubmit(
                params[0] as String,
                userInterface
            )

            ContinuationId.GetSummary_RetryLoading ->
                getSummaryInteractor.onRetryLoading(
                    userInterface
                )

            ContinuationId.GetSummary_LoginCredentialsEnsured ->
                getSummaryInteractor.onLoginCredentialsEnsured(userInterface)

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
        }
    }
}

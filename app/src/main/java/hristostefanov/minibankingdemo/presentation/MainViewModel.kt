package hristostefanov.minibankingdemo.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.usecase.ContinuationId
import hristostefanov.minibankingdemo.usecase.input.StartupInteractor
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val IS_STARTUP_INTERACTOR_ACTIVE_KEY = "isStartupInteractorActive"
private const val IS_PRESENT_SUMMARY_INTERACTIVE_KEY = "isPresentSummaryInteractorActive"
private val LOG_TAG = MainViewModel::class.simpleName

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val startupInteractor: StartupInteractor,
    private val loginSessionRegistry: LoginSessionRegistry,
    private val userInterface: UserInterfaceImpl,
    private val sessionRegistry: LoginSessionRegistry
) : ViewModel() {

    init {
        val isStartupInteractorActive: Boolean? = savedStateHandle[IS_STARTUP_INTERACTOR_ACTIVE_KEY]
        Log.d(LOG_TAG, "Init: isStartupInteractorActive = $isStartupInteractorActive")

        val isPresentSummaryInteractorActive: Boolean? = savedStateHandle[IS_PRESENT_SUMMARY_INTERACTIVE_KEY]
        Log.d(LOG_TAG, "Init: isPresentSummaryInteractorActive - $isPresentSummaryInteractorActive")

        // Should be exactly here - after getting the saved values and before
        // starting interactors.
        keepSavingFlagsForActiveInteractors()

        viewModelScope.launch {
            if (isStartupInteractorActive == true) {
                startupInteractor.resume()
            } else {
                startupInteractor.start(userInterface)
            }

            if (isPresentSummaryInteractorActive == true) {
                loginSessionRegistry.component?.presentAccountsAndRoundupsSummary?.resume()
            }
        }
    }

    private fun keepSavingFlagsForActiveInteractors() {
        startupInteractor.status
            .onEach {
                Log.d(LOG_TAG, "StartupInteractor.status = $it")
                savedStateHandle[IS_STARTUP_INTERACTOR_ACTIVE_KEY] = it.isActive()
            }
            .launchIn(viewModelScope)

        loginSessionRegistry.component?.presentAccountsAndRoundupsSummary?.status
            ?.onEach {
                Log.d(LOG_TAG, "PresentSummaryInteractor.status = $it")
                savedStateHandle[IS_PRESENT_SUMMARY_INTERACTIVE_KEY] = it.isActive()
            }
            ?.launchIn(viewModelScope)
    }

    internal fun executeContinuation(continuationId: String, param: String? = null) =
        viewModelScope.launch {
            Log.d(LOG_TAG, "executeContinuation: continuationId = $continuationId param = $param")

            when (ContinuationId.valueOf(continuationId)) {
                ContinuationId.Startup_LoginCredentialsSubmit -> startupInteractor.onLoginCredentialsSubmit(
                    param!!,
                    userInterface
                )

                ContinuationId.PresentSummary_RetryLoading ->
                    sessionRegistry.component?.presentAccountsAndRoundupsSummary?.onRetryLoading(
                        userInterface
                    )
            }
        }
}
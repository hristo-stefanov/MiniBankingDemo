package hristostefanov.minibankingdemo.presentation

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

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val startupInteractor: StartupInteractor,
    private val userInterface: UserInterfaceImpl,
    private val sessionRegistry: LoginSessionRegistry
) : ViewModel() {

    init {
        startStartupInteractorIfNotActive()
    }

    private fun startStartupInteractorIfNotActive() {
        val isStartupInteractorActive: Boolean? = savedStateHandle[IS_STARTUP_INTERACTOR_ACTIVE_KEY]

        startupInteractor.status
            .onEach {
                savedStateHandle[IS_STARTUP_INTERACTOR_ACTIVE_KEY] = it.isActive()
            }
            .launchIn(viewModelScope)

        if (isStartupInteractorActive != true) {
            viewModelScope.launch {
                startupInteractor.start(userInterface)
            }
        }
    }

    internal fun executeContinuation(continuationId: String, param: String? = null) =
        viewModelScope.launch {
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
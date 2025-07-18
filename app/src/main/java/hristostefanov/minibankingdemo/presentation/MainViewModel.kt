package hristostefanov.minibankingdemo.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.usecase.ContinuationId
import hristostefanov.minibankingdemo.usecase.input.Startup
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val IS_STARTUP_INTERACTOR_ACTIVE_KEY = "isStartupInteractorActive"

@HiltViewModel
class MainViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val startup: Startup,
    val userInterface: UserInterfaceImpl,
    val sessionRegistry: LoginSessionRegistry
) : ViewModel() {

    init {
        val isStartupInteractorActive: Boolean? = savedStateHandle[IS_STARTUP_INTERACTOR_ACTIVE_KEY]

        startup.status
            .onEach {
                savedStateHandle[IS_STARTUP_INTERACTOR_ACTIVE_KEY] = it.isActive()
            }
            .launchIn(viewModelScope)

        if (isStartupInteractorActive != true) {
            viewModelScope.launch {
                startup.onAppStart(userInterface)
            }
        }

    }

    internal fun executeContinuation(continuationId: String, param: String? = null) =
        viewModelScope.launch {
            when (ContinuationId.valueOf(continuationId)) {
                ContinuationId.Startup_LoginCredentialsSubmit -> startup.onLoginCredentialsSubmit(
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
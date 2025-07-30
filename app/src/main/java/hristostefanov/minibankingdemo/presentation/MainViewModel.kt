package hristostefanov.minibankingdemo.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.ui.LOG_INTERACTORS_TAG
import hristostefanov.minibankingdemo.usecase.ContinuationId
import hristostefanov.minibankingdemo.usecase.ContinuationService
import hristostefanov.minibankingdemo.usecase.input.EnsureLoginCredentialsInteractor
import hristostefanov.minibankingdemo.usecase.input.GetSummaryInteractor
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val IS_ENSURE_LOGIN_CREDENTIALS_INTERACTOR_ACTIVE_KEY = "isStartupInteractorActive"
private const val IS_GET_SUMMARY_INTERACTOR_ACTIVE_KEY = "isPresentSummaryInteractorActive"
private const val IS_FRESH_START_KEY = "isFreshStart"


@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val ensureLoginCredentialsInteractor: EnsureLoginCredentialsInteractor,
    private val userInterface: UserInterfaceImpl,
    private val continuationService: ContinuationService,
    private val getSummaryInteractor: GetSummaryInteractor
) : ViewModel() {

    init {
        val isFreshStart: Boolean = savedStateHandle.get<Boolean?>(IS_FRESH_START_KEY) == null
        Log.d(LOG_INTERACTORS_TAG, "isFreshStart = $isFreshStart")

        savedStateHandle[IS_FRESH_START_KEY] = false

        val isEnsureLoginCredentialsInteractorActive: Boolean =
            savedStateHandle[IS_ENSURE_LOGIN_CREDENTIALS_INTERACTOR_ACTIVE_KEY] ?: false
        Log.d(LOG_INTERACTORS_TAG, "Init: isStartupInteractorActive = $isEnsureLoginCredentialsInteractorActive")

        val isGetSummaryInteractorActive: Boolean =
            savedStateHandle[IS_GET_SUMMARY_INTERACTOR_ACTIVE_KEY] ?: false
        Log.d(LOG_INTERACTORS_TAG, "Init: isPresentSummaryInteractorActive - $isGetSummaryInteractorActive")

        // Should be exactly here - after getting the saved values and before
        // starting interactors.
        keepSavingFlagsForActiveInteractors()

        viewModelScope.launch {
            if (isEnsureLoginCredentialsInteractorActive) {
                ensureLoginCredentialsInteractor.resume()
            }

            if (isGetSummaryInteractorActive) {
                getSummaryInteractor.resume()
            }

            if (isFreshStart) {
                getSummaryInteractor.start(userInterface)
            }
        }
    }

    private fun keepSavingFlagsForActiveInteractors() {
        ensureLoginCredentialsInteractor.status
            .onEach {
                Log.d(LOG_INTERACTORS_TAG, "EnsureLoginCredentials.status = $it")
                savedStateHandle[IS_ENSURE_LOGIN_CREDENTIALS_INTERACTOR_ACTIVE_KEY] = it.isActive()
            }
            .launchIn(viewModelScope)

        getSummaryInteractor.status
            .onEach {
                Log.d(LOG_INTERACTORS_TAG, "GetSummaryInteractor.status = $it")
                savedStateHandle[IS_GET_SUMMARY_INTERACTOR_ACTIVE_KEY] = it.isActive()
            }
            .launchIn(viewModelScope)
    }

    // TODO replace with using the channel directly
    internal fun executeContinuation(continuationId: String, param: String? = null) =
        viewModelScope.launch {
            continuationService.executeContinuation(ContinuationId.valueOf(continuationId), param)
        }
}

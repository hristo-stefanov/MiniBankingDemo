package hristostefanov.minibankingdemo.usecase

import android.util.Log
import hristostefanov.minibankingdemo.presentation.Navigation
import hristostefanov.minibankingdemo.presentation.UserInterfaceImpl
import hristostefanov.minibankingdemo.ui.LOG_INTERACTORS_TAG
import hristostefanov.minibankingdemo.usecase.input.EnsureLoginCredentialsInteractor
import hristostefanov.minibankingdemo.usecase.input.GetSummaryInteractor
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import hristostefanov.minibankingdemo.util.NavigationChannel
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

class ContinuationService @Inject constructor(
    @NavigationChannel
    private val navigationChannel: Channel<Navigation>,
    private val userInterface: UserInterfaceImpl,
    private val sessionRegistry: LoginSessionRegistry,
    private val getSummaryInteractor: GetSummaryInteractor,
    private val ensureLoginCredentialsInteractor: EnsureLoginCredentialsInteractor
) {

    suspend fun executeContinuation(continuationId: ContinuationId, param: String? = null) {
        Log.d(LOG_INTERACTORS_TAG, "executeContinuation: continuationId = $continuationId param = $param")

        when (continuationId) {
            ContinuationId.Startup_LoginCredentialsSubmit -> ensureLoginCredentialsInteractor.onLoginCredentialsSubmit(
                param!!,
                userInterface
            )

            ContinuationId.GetSummary_RetryLoading ->
                getSummaryInteractor.onRetryLoading(
                    userInterface
                )

            ContinuationId.TransferRoundUp_AccountSelected ->
                sessionRegistry.component?.transferRoundUpInteractor?.onAccountSelected(
                    param!!,
                    userInterface
                )

            ContinuationId.GetSummary_LoginCredentialsEnsured ->
                getSummaryInteractor.onLoginCredentialsEnsured(userInterface)
        }
    }
}

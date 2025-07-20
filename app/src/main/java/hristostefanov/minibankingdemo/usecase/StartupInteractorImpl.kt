package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.usecase.input.StartupInteractor
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StartupInteractorImpl @Inject constructor(
    private val sessionRegistry: LoginSessionRegistry,
    val tokenStore: TokenStore,
) : StartupInteractor {

    // TODO the status needs to be saved
    private val _status = MutableStateFlow(InteractorStatus.Created)
    override val status: StateFlow<InteractorStatus> = _status.asStateFlow()

    override suspend fun start(userInterface: UserInterface) {
        if (status.value != InteractorStatus.Created)
            throw IllegalStateException()
        _status.emit(InteractorStatus.Started)

        if (sessionRegistry.component == null) {
            userInterface.promptUserToSubmitCredentials(ContinuationId.Startup_LoginCredentialsSubmit)
        } else {
            startPresentSummaryInteractor(userInterface)
            _status.emit(InteractorStatus.Completed)
        }
    }

    override suspend fun resume() {
        _status.emit(InteractorStatus.Started)
    }

    private suspend fun startPresentSummaryInteractor(userInterface: UserInterface) {
        // TODO make it send a start event so as the apropriate UI context can be set,
        // via navigation or so
        sessionRegistry.component?.presentAccountsAndRoundupsSummary?.start(userInterface)
    }

    override suspend fun onLoginCredentialsSubmit(
        loginCredentials: String,
        userInterface: UserInterface
    ) {
        // TODO should this be here?
        tokenStore.token = loginCredentials
        sessionRegistry.createSession(tokenStore.token, "Bearer")

        startPresentSummaryInteractor(userInterface)
        _status.emit(InteractorStatus.Completed)
    }
}
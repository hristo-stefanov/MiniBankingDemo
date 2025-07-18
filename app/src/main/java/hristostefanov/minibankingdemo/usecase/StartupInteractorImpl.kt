package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.usecase.input.StartupInteractor
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
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

    override suspend fun onAppStart(userInterface: UserInterface) {
        if (status.value != InteractorStatus.Created)
            throw IllegalStateException()
        _status.emit(InteractorStatus.Started)

        if (sessionRegistry.component == null) {
            userInterface.promptUserToSubmitCredentials(ContinuationId.Startup_LoginCredentialsSubmit)
        } else {
            val status = startSummaryInteractorAndJoin(userInterface)
            status?.let { _status.emit(it) }
        }
    }

    private suspend fun startSummaryInteractorAndJoin(userInterface: UserInterface): InteractorStatus? {
        sessionRegistry.component?.presentAccountsAndRoundupsSummary?.let { interactor ->
            interactor.start(userInterface)
            return join(interactor.status)
        }
        return null
    }

    private suspend fun join(statusFlow: StateFlow<InteractorStatus>): InteractorStatus =
        status.filter { it.isFinished() }.first()

    override suspend fun onLoginCredentialsSubmit(
        loginCredentials: String,
        userInterface: UserInterface
    ) {
        // TODO should this be here?
        tokenStore.token = loginCredentials
        sessionRegistry.createSession(tokenStore.token, "Bearer")

        val status = startSummaryInteractorAndJoin(userInterface)
        status?.let { _status.emit(it) }
    }
}
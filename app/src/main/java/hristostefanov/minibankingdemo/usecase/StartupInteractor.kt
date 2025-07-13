package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.usecase.input.Startup
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StartupInteractor @Inject constructor(
    private val sessionRegistry: LoginSessionRegistry,
    val tokenStore: TokenStore,
) : Startup {

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
            startAndJoinChild(userInterface)
        }
    }

    private suspend fun startAndJoinChild(userInterface: UserInterface) {
        sessionRegistry.component?.presentAccountsAndRoundupsSummary?.let {
            it.start(userInterface)
            val childFinishedStatus = it.status.filter { it.isFinished() }.single()
            _status.emit(childFinishedStatus)
        }
    }

    override suspend fun onLoginCredentialsSubmit(
        loginCredentials: String,
        userInterface: UserInterface
    ) {
        // TODO should this be here?
        tokenStore.token = loginCredentials
        sessionRegistry.createSession(tokenStore.token, "Bearer")

        startAndJoinChild(userInterface)
    }
}
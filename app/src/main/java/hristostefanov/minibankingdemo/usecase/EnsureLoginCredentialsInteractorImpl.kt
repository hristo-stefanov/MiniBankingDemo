package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.presentation.MainViewModel
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.usecase.input.EnsureLoginCredentialsInteractor
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import hristostefanov.minibankingdemo.util.ContinuationChannel
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnsureLoginCredentialsInteractorImpl @Inject constructor(
    private val sessionRegistry: LoginSessionRegistry,
    val tokenStore: TokenStore,
    @ContinuationChannel
    val continuationChannel: Channel<Continuation>
) : EnsureLoginCredentialsInteractor {

    // TODO the status needs to be saved
    private val _status = MutableStateFlow(InteractorStatus.Created)
    override val status: StateFlow<InteractorStatus> = _status.asStateFlow()

    // TODO needs to be saved
    private lateinit var stopContinuationId: ContinuationId

    override suspend fun start(userInterface: UserInterface, stopContinuationId: ContinuationId) {
        if (status.value != InteractorStatus.Created)
            throw IllegalStateException()
        _status.emit(InteractorStatus.Started)

        this.stopContinuationId = stopContinuationId

        if (sessionRegistry.component == null) {
            userInterface.promptUserToSubmitCredentials(ContinuationId.Startup_LoginCredentialsSubmit)
        } else {
            continuationChannel.send(Continuation(stopContinuationId))

            _status.emit(InteractorStatus.Completed)
        }
    }

    override suspend fun resume() {
        _status.emit(InteractorStatus.Started)
    }

    override suspend fun onLoginCredentialsSubmit(
        loginCredentials: String,
        userInterface: UserInterface
    ) {
        // TODO should this be here?
        tokenStore.token = loginCredentials
        sessionRegistry.createSession(tokenStore.token, "Bearer")

        continuationChannel.send(Continuation(stopContinuationId))

        _status.emit(InteractorStatus.Completed)
    }
}
package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.usecase.input.EnsureLoginCredentialsInteractor
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import hristostefanov.minibankingdemo.util.ContinuationChannel
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnsureLoginCredentialsInteractorImpl @Inject constructor(
    private val sessionRegistry: LoginSessionRegistry,
    val tokenStore: TokenStore,
    @ContinuationChannel
    val continuationChannel: Channel<Continuation>,
    private val lifecycle: InteractorLifecycleImpl,
) : EnsureLoginCredentialsInteractor, InteractorLifecycle by lifecycle {

    // TODO needs to be saved
    private lateinit var stopContinuationId: ContinuationId

    override suspend fun start(userInterface: UserInterface, stopContinuationId: ContinuationId) {
        if (lifecycle.status == InteractorStatus.Started)
            throw IllegalStateException()

        lifecycle.setStatus(InteractorStatus.Started)

        this.stopContinuationId = stopContinuationId

        if (sessionRegistry.component == null) {
            userInterface.promptUserToSubmitCredentials(ContinuationId.Startup_LoginCredentialsSubmit)
        } else {
            continuationChannel.send(Continuation(stopContinuationId))
            lifecycle.setStatus(InteractorStatus.Completed)
        }
    }

    override suspend fun onLoginCredentialsSubmit(
        loginCredentials: String,
        userInterface: UserInterface
    ) {
        // TODO should this be here?
        tokenStore.token = loginCredentials
        sessionRegistry.createSession(tokenStore.token, "Bearer")

        continuationChannel.send(Continuation(stopContinuationId))

        lifecycle.setStatus(InteractorStatus.Completed)
    }
}
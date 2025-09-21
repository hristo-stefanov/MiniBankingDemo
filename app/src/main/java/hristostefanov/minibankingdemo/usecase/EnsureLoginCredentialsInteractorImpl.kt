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
    override suspend fun start(userInterface: UserInterface): Outcome {
        if (lifecycle.status == InteractorStatus.Started)
            throw IllegalStateException()

        lifecycle.setStatus(InteractorStatus.Started)

        if (sessionRegistry.component == null) {
            val result = userInterface.promptUserToSubmitCredentials()
            if (result == null) {
                val outcome = Outcome.Cancelled
                lifecycle.setFinishOutcome(outcome)
                return outcome
            } else {
                // TODO should this be here?
                tokenStore.token = result
                sessionRegistry.createSession(tokenStore.token, "Bearer")

                val outcome = Outcome.Completed(Unit)
                lifecycle.setFinishOutcome(outcome)
                return outcome
            }
        } else {
            val outcome = Outcome.Completed(Unit)
            lifecycle.setFinishOutcome(outcome)
            return outcome
        }
    }
}
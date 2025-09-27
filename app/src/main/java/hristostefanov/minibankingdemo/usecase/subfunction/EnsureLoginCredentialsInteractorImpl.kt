package hristostefanov.minibankingdemo.usecase.subfunction

import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.usecase.Outcome
import hristostefanov.minibankingdemo.usecase.input.EnsureLoginCredentialsInteractor
import hristostefanov.minibankingdemo.usecase.output.EnsureLoginCredentialsUI
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnsureLoginCredentialsInteractorImpl @Inject constructor(
    private val sessionRegistry: LoginSessionRegistry,
    val tokenStore: TokenStore,
) : EnsureLoginCredentialsInteractor {
    override suspend fun start(userInterface: EnsureLoginCredentialsUI): Outcome {
        if (sessionRegistry.component == null) {
            val result = userInterface.promptUserToSubmitCredentials()
            if (result == null) {
                return Outcome.Cancelled
            } else {
                // TODO should this be here?
                tokenStore.token = result
                sessionRegistry.createSession(tokenStore.token, "Bearer")

                return Outcome.Completed<Unit>(Unit)
            }
        } else {
            return Outcome.Completed(Unit)
        }
    }
}
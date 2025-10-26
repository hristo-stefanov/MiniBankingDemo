package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.usecase.input.EnsureLoginCredentialsInteractor
import hristostefanov.minibankingdemo.usecase.input.Outcome
import hristostefanov.minibankingdemo.usecase.output.EnsureLoginCredentialsUI
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnsureLoginCredentialsInteractorImpl @Inject constructor(
    private val sessionRegistry: LoginSessionRegistry,
    private val tokenStore: TokenStore,
    private val ensureLoginCredentialsUI: EnsureLoginCredentialsUI
) : EnsureLoginCredentialsInteractor {
    override suspend fun start(): Outcome {
        if (sessionRegistry.component == null) {
            val result = ensureLoginCredentialsUI.promptUserToSubmitCredentials()
            if (result == null) {
                return Outcome.Cancelled
            } else {
                tokenStore.setToken(result)
                sessionRegistry.createSession(result, "Bearer")

                return Outcome.Completed<Unit>(Unit)
            }
        } else {
            return Outcome.Completed(Unit)
        }
    }
}
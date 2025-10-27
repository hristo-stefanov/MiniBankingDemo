package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.usecase.input.Cancellation
import hristostefanov.minibankingdemo.usecase.input.Completion
import hristostefanov.minibankingdemo.usecase.input.EnsureLoginCredentialsInteractor
import hristostefanov.minibankingdemo.usecase.input.Status
import hristostefanov.minibankingdemo.usecase.output.EnsureLoginCredentialsUI
import hristostefanov.minibankingdemo.usecase.input.status
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnsureLoginCredentialsInteractorImpl @Inject constructor(
    private val sessionRegistry: LoginSessionRegistry,
    private val tokenStore: TokenStore,
    private val ensureLoginCredentialsUI: EnsureLoginCredentialsUI
) : EnsureLoginCredentialsInteractor {
    override suspend fun invoke(): Status {
        if (sessionRegistry.component == null) {
            val result = ensureLoginCredentialsUI.promptUserToSubmitCredentials()

            if (result == null) {
                return Cancellation.status()
            } else {
                tokenStore.setToken(result)
                sessionRegistry.createSession(result, "Bearer")

                return Completion.status()
            }
        } else {
            return Completion.status()
        }
    }
}
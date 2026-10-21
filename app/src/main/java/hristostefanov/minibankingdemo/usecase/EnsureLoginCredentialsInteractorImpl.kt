package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.usecase.input.Termination.Cancellation
import hristostefanov.minibankingdemo.usecase.input.Completion
import hristostefanov.minibankingdemo.usecase.input.EnsureLoginCredentialsInteractor
import hristostefanov.minibankingdemo.usecase.input.Status
import hristostefanov.minibankingdemo.usecase.output.EnsureLoginCredentialsUI
import hristostefanov.minibankingdemo.usecase.input.status
import hristostefanov.minibankingdemo.util.Session
import kotlinx.coroutines.async
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnsureLoginCredentialsInteractorImpl @Inject constructor(
    private val tokenStore: TokenStore,
    private val session: Session,
    private val ensureLoginCredentialsUI: EnsureLoginCredentialsUI
) : EnsureLoginCredentialsInteractor {
    override suspend fun invoke(): Status = session.requireTokenComponent.scope.async {
        if (!session.state.value.hasToken) {
            val result = ensureLoginCredentialsUI.promptUserToSubmitCredentials()

            result.fold({
                Cancellation.status()
            }, { credentials ->
                tokenStore.setToken(credentials)
                session.setToken(credentials, "Bearer")

                Completion.status()
            })
        } else {
            Completion.status()
        }
    }.await()
}
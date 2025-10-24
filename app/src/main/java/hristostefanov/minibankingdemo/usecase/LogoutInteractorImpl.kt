package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogoutInteractorImpl @Inject constructor(
    private val tokenStore: TokenStore,
    private val loginSessionRegistry: LoginSessionRegistry,
) : LogoutInteractor {

    override suspend fun start(): Outcome {
        tokenStore.setToken(null)
        loginSessionRegistry.close()

        return Outcome.Completed(Unit)
    }
}
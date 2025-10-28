package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.usecase.input.Completion
import hristostefanov.minibankingdemo.usecase.input.LogoutInteractor
import hristostefanov.minibankingdemo.usecase.input.Status
import hristostefanov.minibankingdemo.usecase.input.status
import hristostefanov.minibankingdemo.util.LoginSessionData
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogoutInteractorImpl @Inject constructor(
    private val tokenStore: TokenStore,
    private val loginSessionRegistry: LoginSessionRegistry,
    private val loginSessionData: LoginSessionData,
) : LogoutInteractor {

    override suspend fun invoke(): Status {
        tokenStore.setToken(null)
        loginSessionRegistry.close()
        loginSessionData.clear()

        return Completion.status()
    }
}
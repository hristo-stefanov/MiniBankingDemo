package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogoutInteractorImpl @Inject constructor(
    private val lifecycle: InteractorLifecycleImpl,
    private val tokenStore: TokenStore,
    private val loginSessionRegistry: LoginSessionRegistry,
) : LogoutInteractor,
    InteractorLifecycle by lifecycle {

    override suspend fun start(userInterface: UserInterface) {
        tokenStore.token = ""
        loginSessionRegistry.close()

        lifecycle.setStatus(InteractorStatus.Completed)
    }
}
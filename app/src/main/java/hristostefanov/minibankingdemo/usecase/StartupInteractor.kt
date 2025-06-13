package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.usecase.input.PresentAccountsAndRoundupsSummary
import hristostefanov.minibankingdemo.usecase.input.Startup
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class StartupInteractor @Inject constructor(
    private val sessionRegistry: LoginSessionRegistry,
    val tokenStore: TokenStore,
) : Startup {
    override suspend fun launchApp(userInterface: UserInterface) {
        if (tokenStore.token.isEmpty()) {
            val token = userInterface.promptUserToSubmitCredentials()

            tokenStore.token = token
        }
        sessionRegistry.createSession(tokenStore.token, "Bearer")
        // TODO createSession() can return the component to avoid handling the case of null
        // component here
        val result = sessionRegistry.component!!.presentAccountsAndRoundupsSummary(userInterface)

        // TODO return result
    }
}
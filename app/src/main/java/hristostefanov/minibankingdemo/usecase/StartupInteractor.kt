package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.usecase.input.Startup
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import javax.inject.Inject

class StartupInteractor @Inject constructor(
    private val sessionRegistry: LoginSessionRegistry,
    val tokenStore: TokenStore,
) : Startup {
    override suspend fun launchApp(userInterface: UserInterface) {

        if (sessionRegistry.component == null) {
            userInterface.promptUserToSubmitCredentials()
        } else {
            val result = sessionRegistry.component?.presentAccountsAndRoundupsSummary(userInterface)
            // TODO return result
        }

    }

    override suspend fun onLoginCredentialsSubmit(
        loginCredentials: String,
        userInterface: UserInterface
    ) {
        tokenStore.token = loginCredentials
        sessionRegistry.createSession(tokenStore.token, "Bearer")

        val result = sessionRegistry.component?.presentAccountsAndRoundupsSummary(userInterface)
        // TODO return result
    }
}
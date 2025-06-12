package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.usecase.input.PresentAccountsAndRoundupsSummary
import hristostefanov.minibankingdemo.usecase.input.Startup
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class StartupInteractor @Inject constructor(
    val userInterface: UserInterface,
    val tokenStore: TokenStore,
    val presentAccountsAndRoundupsSummary: PresentAccountsAndRoundupsSummary,
    val dispatcher: CoroutineDispatcher,
) : Startup {
    private val coroutineScope = CoroutineScope(dispatcher)

    override suspend fun launchApp() {
        if (tokenStore.token.isEmpty()) {
            val token = userInterface.promptUserToSubmitCredentials()

            tokenStore.token = token
        }
        val result = presentAccountsAndRoundupsSummary()
    }
}
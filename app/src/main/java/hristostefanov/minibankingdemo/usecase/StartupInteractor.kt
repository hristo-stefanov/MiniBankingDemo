package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

interface StartupOutputBoundary {
    fun promptUserToSubmitCredentials()
}

class StartupInteractor @Inject constructor(
    val output: StartupOutputBoundary,
    val tokenStore: TokenStore,
    val presentAccountsAndRoundupsSummaryInteractor: PresentAccountsAndRoundupsSummaryInteractor,
    val dispatcher: CoroutineDispatcher,
) {
    private val coroutineScope = CoroutineScope(dispatcher)

    fun launchApp() {
        if (tokenStore.token.isEmpty()) {
            output.promptUserToSubmitCredentials()
        } else {
            coroutineScope.launch(dispatcher) {
                presentAccountsAndRoundupsSummaryInteractor()
            }
        }
    }

    fun submitLoginCredentials(token: String) {
        tokenStore.token = token

        coroutineScope.launch(dispatcher) {
            presentAccountsAndRoundupsSummaryInteractor()
        }
    }
}
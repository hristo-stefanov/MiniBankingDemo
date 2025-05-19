package hristostefanov.minibankingdemo.business.interactors.startup

import hristostefanov.minibankingdemo.business.interactors.shared.PresentAccountsAndRoundupsInteractor
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class StartupInteractor @Inject constructor(
    val output: StartupOutputBoundary,
    val tokenStore: TokenStore,
    val presentAccountsAndRoundupsInteractor: PresentAccountsAndRoundupsInteractor,
    val dispatcher: CoroutineDispatcher,
) {
    private val coroutineScope = CoroutineScope(dispatcher)

    fun launchApp() {
        if (tokenStore.token.isEmpty()) {
            output.promptUserToSubmitCredentials()
        } else {
            coroutineScope.launch(dispatcher) {
                presentAccountsAndRoundupsInteractor()
            }
        }
    }

    fun submitLoginCredentials(token: String) {
        tokenStore.token = token

        coroutineScope.launch(dispatcher) {
            presentAccountsAndRoundupsInteractor()
        }
    }
}
package hristostefanov.minibankingdemo.business.interactors.startup

import hristostefanov.minibankingdemo.business.interactors.shared.ShowAccountsAndRoundupsInteractor
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import javax.inject.Inject

class StartupInteractor @Inject constructor(
    val output: StartupOutputBoundary,
    val tokenStore: TokenStore,
    val showAccountsAndRoundupsInteractor: ShowAccountsAndRoundupsInteractor,
) {
    fun launchApp() {
        if (tokenStore.token.isEmpty()) {
            output.promptUserToSubmitCredentials()
        } else {
            showAccountsAndRoundupsInteractor.execute()
        }
    }

    fun submitLoginCredentials(token: String) {
        tokenStore.token = token

        showAccountsAndRoundupsInteractor.execute()
    }
}
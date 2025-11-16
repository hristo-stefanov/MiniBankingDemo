package hristostefanov.minibankingdemo.presentation

import hristostefanov.minibankingdemo.usecase.output.EnsureLoginCredentialsUI
import javax.inject.Inject

class EnsureLoginCredentialsUiImpl @Inject constructor(private val mainUI: MainUI) : EnsureLoginCredentialsUI {
    override suspend fun promptUserToSubmitCredentials(): String? = mainUI.promptUserToSubmitCredentials()
}
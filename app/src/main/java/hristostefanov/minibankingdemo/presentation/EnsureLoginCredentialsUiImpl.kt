package hristostefanov.minibankingdemo.presentation

import arrow.core.left
import arrow.core.right
import hristostefanov.minibankingdemo.usecase.output.Cancel
import hristostefanov.minibankingdemo.usecase.output.EnsureLoginCredentialsUI
import javax.inject.Inject

class EnsureLoginCredentialsUiImpl @Inject constructor(private val mainUI: MainUI) : EnsureLoginCredentialsUI {
    override suspend fun promptUserToSubmitCredentials() =
        mainUI.promptUserToSubmitCredentials().fold({ Cancel.left() }, { it.right() })
}
package hristostefanov.minibankingdemo.presentation

import hristostefanov.minibankingdemo.usecase.output.AccountsAndRoundUpsSummary
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import hristostefanov.minibankingdemo.util.NavigationChannel
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject
import hristostefanov.minibankingdemo.NavGraphXmlDirections
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Singleton

@Singleton
class UserInterfaceImpl @Inject constructor(
    @NavigationChannel
    private val navigationChannel: Channel<Navigation>,
) : UserInterface {

    private val _summary = MutableStateFlow<AccountsAndRoundUpsSummary?>(null)
    val summary = _summary.asStateFlow()

    override suspend fun promptUserToSubmitCredentials() {
        navigationChannel.send(Navigation.Forward(NavGraphXmlDirections.toLoginDestination()))
    }

    override fun present(summary: AccountsAndRoundUpsSummary) {
        _summary.value = summary
    }

    override suspend fun promptUserToRetryRecovery(message: String, continuationId: String) {
        navigationChannel.send(
            Navigation.Forward(
                NavGraphXmlDirections.toRetryDialog(
                    // TODO pass this as parameter for cases like swipe-to-refresh
                    // and explicit commands that should be cancellable
                    // TODO alternatively cancelling uncancelable use case can close the app
                    isCancelable = false,
                    message = message,
                    continuationId = continuationId
                )
            )
        )
    }
}
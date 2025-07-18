package hristostefanov.minibankingdemo.presentation

import hristostefanov.minibankingdemo.usecase.output.AccountsAndRoundUpsSummary
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import hristostefanov.minibankingdemo.util.NavigationChannel
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject
import hristostefanov.minibankingdemo.NavGraphXmlDirections
import hristostefanov.minibankingdemo.usecase.ContinuationId
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

    override suspend fun promptUserToSubmitCredentials(continuationId: ContinuationId) {
        navigationChannel.send(Navigation.Forward(NavGraphXmlDirections.toLoginDestination(continuationId.name)))
    }

    override fun presentSummary(summary: AccountsAndRoundUpsSummary) {
        _summary.value = summary
    }

    override suspend fun promptUserToRetryRecovery(message: String, isCancellable: Boolean, continuationId: ContinuationId) {
        navigationChannel.send(
            Navigation.Forward(
                NavGraphXmlDirections.toRetryDialog(
                    // TODO how about cancelling uncancelable use case to close the app?
                    isCancelable = isCancellable,
                    message = message,
                    continuationId = continuationId.name
                )
            )
        )
    }

    override suspend fun presentMessage(message: String) {
        // TODO make it not navigate - display a temp
        navigationChannel.send(
            Navigation.Message(message)
        )
    }
}
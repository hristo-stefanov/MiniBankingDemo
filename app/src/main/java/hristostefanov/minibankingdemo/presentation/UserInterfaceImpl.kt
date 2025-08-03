package hristostefanov.minibankingdemo.presentation

import hristostefanov.minibankingdemo.usecase.output.Summary
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import hristostefanov.minibankingdemo.util.NavigationChannel
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject
import hristostefanov.minibankingdemo.NavGraphXmlDirections
import hristostefanov.minibankingdemo.R
import hristostefanov.minibankingdemo.ui.AccountsFragmentDirections
import hristostefanov.minibankingdemo.business.entities.SavingsGoal
import hristostefanov.minibankingdemo.ui.SavingsGoalsFragmentDirections
import hristostefanov.minibankingdemo.usecase.ContinuationId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal
import java.util.Currency
import javax.inject.Singleton

@Singleton
class UserInterfaceImpl @Inject constructor(
    @NavigationChannel
    private val navigationChannel: Channel<Navigation>,
) : UserInterface {

    private val _summary = MutableStateFlow<Summary?>(null)
    val summary = _summary.asStateFlow()

    override suspend fun promptUserToSubmitCredentials(continuationId: ContinuationId) {
        navigationChannel.send(Navigation.Forward(NavGraphXmlDirections.toLoginDestination(continuationId.name)))
    }

    override fun presentSummary(summary: Summary) {
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

    override suspend fun promptUserToSelectSavingsGoal(message: String, savingsGoals: List<SavingsGoal>) {
        val displaySavingsGoals = savingsGoals.map { DisplaySavingsGoal(it.id, it.name) }
        navigationChannel.send(
            Navigation.Forward(
                AccountsFragmentDirections.actionToSavingsGoalsDestination(
                    message,
                    displaySavingsGoals.toTypedArray()
                )
            )
        )
    }

    override suspend fun promptUserToSubmitGoalName(continuationId: ContinuationId) {
        navigationChannel.send(
            Navigation.Forward(
                SavingsGoalsFragmentDirections.actionToCreateSavingsGoalDestination(continuationId.name)
            )
        )
    }

    override suspend fun closeTransferRoundUpUI() {
        navigationChannel.send(Navigation.Before(R.id.savingsGoalsDestination))
    }

    override suspend fun promptUserToConfirmTransfer(
        roundUpAmount: BigDecimal,
        accountCurrency: Currency,
        savingsGoalNam: String,
        continuationId: ContinuationId
    ) {
        navigationChannel.send(
            Navigation.Forward(
                SavingsGoalsFragmentDirections.actionToTransferConfirmationDestination(
                    savingsGoalName = savingsGoalNam,
                    roundUpAmount = roundUpAmount,
                    accountCurrency = accountCurrency,
                    continuationId = continuationId.name,
                )
            )
        )
    }

    override suspend fun closeCreateSavingsGoalUI() {
        navigationChannel.send(Navigation.Backward)
    }
}
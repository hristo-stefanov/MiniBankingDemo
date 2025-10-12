package hristostefanov.minibankingdemo.presentation

import hristostefanov.minibankingdemo.usecase.output.Summary
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import hristostefanov.minibankingdemo.util.NavigationChannel
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject
import hristostefanov.minibankingdemo.NavGraphXmlDirections
import hristostefanov.minibankingdemo.ui.AccountsFragmentDirections
import hristostefanov.minibankingdemo.business.entities.SavingsGoal
import hristostefanov.minibankingdemo.ui.SavingsGoalsFragmentDirections
import java.math.BigDecimal
import java.util.Currency
import javax.inject.Singleton
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine

@Singleton
class UserInterfaceImpl @Inject constructor(
    @NavigationChannel
    private val navigationChannel: Channel<Navigation>,
) : UserInterface {

    // TODO handle cancellation in a explicit way - with a tagged union or monad
    lateinit var loginCredentialsContinuation: Continuation<String?>

    // TODO consider the case of multiple async operations asking for retry confirmation
    // true - confirmed , false - cancelled
    lateinit var retryRecoveryContinuation: Continuation<Boolean>

    override suspend fun promptUserToSubmitCredentials(): String? {
        navigationChannel.send(Navigation.Forward(NavGraphXmlDirections.toLoginDestination()))
        return suspendCoroutine {
            loginCredentialsContinuation = it
        }
    }


    override suspend fun promptUserToRetryRecovery(message: String, isCancellable: Boolean): Boolean {
        navigationChannel.send(
            Navigation.Forward(
                NavGraphXmlDirections.toRetryDialog(
                    // TODO how about cancelling uncancelable use case to close the app?
                    isCancelable = isCancellable,
                    message = message,
                )
            )
        )
        return suspendCoroutine {
            retryRecoveryContinuation = it
        }
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

    override suspend fun promptUserToConfirmTransfer(
        roundUpAmount: BigDecimal,
        accountCurrency: Currency,
        savingsGoalNam: String,
    ) {
        navigationChannel.send(
            Navigation.Forward(
                SavingsGoalsFragmentDirections.actionToTransferConfirmationDestination(
                    savingsGoalName = savingsGoalNam,
                    roundUpAmount = roundUpAmount,
                    accountCurrency = accountCurrency,
                )
            )
        )
    }
}
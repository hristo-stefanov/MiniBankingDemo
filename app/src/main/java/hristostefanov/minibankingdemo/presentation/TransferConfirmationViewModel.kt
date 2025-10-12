package hristostefanov.minibankingdemo.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.R
import hristostefanov.minibankingdemo.presentation.dependences.AmountFormatter
import hristostefanov.minibankingdemo.ui.TransferConfirmationFragmentArgs
import hristostefanov.minibankingdemo.usecase.Outcome
import hristostefanov.minibankingdemo.usecase.input.TransferRoundUpInteractor
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import hristostefanov.minibankingdemo.util.NavigationChannel
import hristostefanov.minibankingdemo.util.StringSupplier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val NAVIGATION_DELAY_MS = 2000L

@HiltViewModel
class TransferConfirmationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val stringSupplier: StringSupplier,
    private val amountFormatter: AmountFormatter,
    @NavigationChannel
    private val navigationChannel: Channel<Navigation>,
    private val userInterface: UserInterface,
    private val transferRoundUpInteractor: TransferRoundUpInteractor,
    private val loginSessionRegistry: LoginSessionRegistry,
) : ViewModel() {

    private val args = TransferConfirmationFragmentArgs.fromSavedStateHandle(savedStateHandle)

    private val _info = MutableLiveData("")
    val info: LiveData<String> = _info

    init {
        val amountFormatted = amountFormatter.format(
            args.roundUpAmount,
            args.accountCurrency.currencyCode
        )
        _info.value = stringSupplier.get(R.string.transferInfo)
            .format(amountFormatted, args.savingsGoalName)
    }

    fun onConfirmCommand() {
        viewModelScope.launch {
            with(loginSessionRegistry.requireComponent.data) {
                val outcome = transferRoundUpInteractor.start(
                    accountId = selectedAccount.accountId,
                    accountCurrency = selectedAccount.currency,
                    savingsGoalId = savingsGoalId,
                    roundUpAmount = selectedAccount.roundUp,
                    userInterface = userInterface,
                )
                if (outcome is Outcome.Completed<*>) {
                    navigationChannel.send(Navigation.Before(R.id.savingsGoalsDestination))
                }
            }
        }
    }
}
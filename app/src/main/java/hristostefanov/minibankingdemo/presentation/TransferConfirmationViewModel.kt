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
import hristostefanov.minibankingdemo.usecase.input.TransferRoundUpInteractor
import hristostefanov.minibankingdemo.usecase.input.isFailure
import hristostefanov.minibankingdemo.util.LoginSessionData
import hristostefanov.minibankingdemo.util.MainCommandChannel
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
    @MainCommandChannel
    private val mainCommandChannel: Channel<MainCommand>,
    private val transferRoundUpInteractor: TransferRoundUpInteractor,
    private val loginSessionData: LoginSessionData,
    private val mainUI: MainUI
) : ViewModel() {

    private val args = TransferConfirmationFragmentArgs.fromSavedStateHandle(savedStateHandle)

    private val _info = MutableLiveData("")
    val info: LiveData<String> = _info

    init {
        val amountFormatted = amountFormatter.format(
            args.roundUpAmount,
            args.accountCurrency.currencyCode
        )
        _info.value = stringSupplier.get(R.string.transferInfo, amountFormatted, args.savingsGoalName)
    }

    fun onConfirmCommand() {
        viewModelScope.launch {
            with(loginSessionData) {
                selectedAccount?.let { account ->
                    val status = transferRoundUpInteractor(
                        accountId = account.accountId,
                        accountCurrency = account.currency,
                        savingsGoalId = savingsGoalId,
                        roundUpAmount = account.roundUp,
                    )

                    mainUI.presentStatus(status)

                    if (!status.isFailure()) {
                        mainCommandChannel.send(MainCommand.NavigateBefore(R.id.savingsGoalsDestination))
                    }
                }
            }
        }
    }
}
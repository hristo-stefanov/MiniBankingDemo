package hristostefanov.minibankingdemo.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.R
import hristostefanov.minibankingdemo.presentation.dependences.AmountFormatter
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
    private val stringSupplier: StringSupplier,
    private val amountFormatter: AmountFormatter,
    @MainCommandChannel
    private val mainCommandChannel: Channel<MainCommand>,
    private val transferRoundUpInteractor: TransferRoundUpInteractor,
    private val loginSessionData: LoginSessionData,
    private val mainUI: MainUI
) : ViewModel() {

    private val _info = MutableLiveData("")
    val info: LiveData<String> = _info

    init {
        loginSessionData.selectedAccount?.let { account ->
            val amountFormatted = amountFormatter.format(
                account.roundUp,
                account.currency.currencyCode
            )
            _info.value = stringSupplier.get(R.string.transferInfo, amountFormatted, loginSessionData.savingsGoalName)
        }
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
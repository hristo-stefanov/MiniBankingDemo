package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import hristostefanov.starlingdemo.NavGraphXmlDirections
import hristostefanov.starlingdemo.R
import hristostefanov.starlingdemo.business.dependences.ServiceException
import hristostefanov.starlingdemo.business.interactors.AddMoneyIntoGoalInteractor
import hristostefanov.starlingdemo.presentation.dependences.AmountFormatter
import hristostefanov.starlingdemo.ui.TransferConfirmationFragmentDirections
import hristostefanov.starlingdemo.util.StringSupplier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

private const val NAVIGATION_DELAY_MS = 2000L

class TransferConfirmationViewModel @Inject constructor(
    private val _interactor: AddMoneyIntoGoalInteractor,
    private val _sessionState: SessionState,
    private val _locale: Locale,
    private val _stringSupplier: StringSupplier,
    private val _amountFormatter: AmountFormatter
) : ViewModel() {

    private val _acknowledgementChannel = Channel<String>()
    val acknowledgementChannel: ReceiveChannel<String> = _acknowledgementChannel

    private val _info = MutableLiveData<String>("")
    val info: LiveData<String> = _info

    private val _navigationChannel = Channel<NavDirections>()
    val navigationChannel: ReceiveChannel<NavDirections> = _navigationChannel

    init {
        val amountFormatted = _amountFormatter.format(
            _sessionState.roundUpAmount,
            _sessionState.accountCurreny.currencyCode,
            _locale
        )
        _info.value = _stringSupplier.get(R.string.transferInfo)
            .format(amountFormatted, _sessionState.savingsGoal.name)
    }

    fun onConfirmCommand() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _interactor.execute(
                    _sessionState.accountId,
                    _sessionState.savingsGoal.id,
                    _sessionState.accountCurreny,
                    _sessionState.roundUpAmount
                )
                _acknowledgementChannel.send(_stringSupplier.get(R.string.success))
                delay(NAVIGATION_DELAY_MS)

                _navigationChannel.send(TransferConfirmationFragmentDirections.actionToAccountsDestination())
            } catch (e: ServiceException) {
                e.localizedMessage?.also {
                    _navigationChannel.send(NavGraphXmlDirections.toErrorDialog(it))
                }
            }
        }
    }
}
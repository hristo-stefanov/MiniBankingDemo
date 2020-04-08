package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.*
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

/**
 * Expected arguments passed through [SavedStateHandle]:
 * [ACCOUNT_CURRENCY_KEY], [ACCOUNT_ID_KEY], [ROUND_UP_AMOUNT_KEY] and [SAVINGS_GOAL_KEY]
 */
class TransferConfirmationViewModel constructor(
    private val _state: SavedStateHandle
) : ViewModel() {
    @Inject
    internal lateinit var _interactor: AddMoneyIntoGoalInteractor
    @Inject
    internal lateinit var _locale: Locale
    @Inject
    internal lateinit var _stringSupplier: StringSupplier
    @Inject
    internal lateinit var _amountFormatter: AmountFormatter


    private val _acknowledgementChannel = Channel<String>()
    val acknowledgementChannel: ReceiveChannel<String> = _acknowledgementChannel

    private val _info = MutableLiveData("")
    val info: LiveData<String> = _info

    private val _navigationChannel = Channel<NavDirections>()
    val navigationChannel: ReceiveChannel<NavDirections> = _navigationChannel

    @Inject
    internal fun init() {
        val amountFormatted = _amountFormatter.format(
            _state.roundUpAmount,
            _state.accountCurrency.currencyCode,
            _locale
        )
        _info.value = _stringSupplier.get(R.string.transferInfo)
             .format(amountFormatted, _state.savingsGoal.name)
    }

    fun onConfirmCommand() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _interactor.execute(
                    _state.accountId,
                    _state.savingsGoal.id,
                    _state.accountCurrency,
                    _state.roundUpAmount
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
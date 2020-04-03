package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.*
import androidx.navigation.NavDirections
import hristostefanov.starlingdemo.NavGraphXmlDirections
import hristostefanov.starlingdemo.R
import hristostefanov.starlingdemo.business.dependences.ServiceException
import hristostefanov.starlingdemo.business.entities.SavingsGoal
import hristostefanov.starlingdemo.business.interactors.AddMoneyIntoGoalInteractor
import hristostefanov.starlingdemo.presentation.dependences.AmountFormatter
import hristostefanov.starlingdemo.ui.TransferConfirmationFragmentDirections
import hristostefanov.starlingdemo.util.StringSupplier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

private const val NAVIGATION_DELAY_MS = 2000L

class TransferConfirmationViewModel constructor(
    private val _savedStateHandle: SavedStateHandle
) : ViewModel() {


    @Inject
    internal lateinit var _interactor: AddMoneyIntoGoalInteractor
    @Inject
    internal lateinit var _locale: Locale
    @Inject
    internal lateinit var _stringSupplier: StringSupplier
    @Inject
    internal lateinit var _amountFormatter: AmountFormatter

    private val _savingsGoal: SavingsGoal = _savedStateHandle[SAVINGS_GOAL_KEY]
        ?: throw java.lang.IllegalArgumentException(SAVINGS_GOAL_KEY)
    private val _accountId: String = _savedStateHandle[ACCOUNT_ID_KEY]
        ?: throw IllegalArgumentException(ACCOUNT_ID_KEY)
    private val _accountCurrency: Currency = _savedStateHandle[ACCOUNT_CURRENCY_KEY]
        ?: throw IllegalArgumentException(ACCOUNT_CURRENCY_KEY)
    private val _roundUpAmount: BigDecimal = _savedStateHandle[ROUND_UP_AMOUNT_KEY]
        ?: throw IllegalArgumentException(ROUND_UP_AMOUNT_KEY)


    private val _acknowledgementChannel = Channel<String>()
    val acknowledgementChannel: ReceiveChannel<String> = _acknowledgementChannel

    private val _info = MutableLiveData("")
    val info: LiveData<String> = _info

    private val _navigationChannel = Channel<NavDirections>()
    val navigationChannel: ReceiveChannel<NavDirections> = _navigationChannel

    @Inject
    internal fun init() {
        val amountFormatted = _amountFormatter.format(
            _roundUpAmount,
            _accountCurrency.currencyCode,
            _locale
        )
        _info.value = _stringSupplier.get(R.string.transferInfo)
             .format(amountFormatted, _savingsGoal.name)
    }

    fun onConfirmCommand() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _interactor.execute(
                    _accountId,
                    _savingsGoal.id,
                    _accountCurrency,
                    _roundUpAmount
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
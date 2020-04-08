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
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

private const val NAVIGATION_DELAY_MS = 2000L

/**
 * Expected arguments passed through [SavedStateHandle]:
 * [ACCOUNT_CURRENCY_ARG_KEY], [ACCOUNT_ID_ARG_KEY], [ROUND_UP_AMOUNT_ARG_KEY] and [SAVINGS_GOAL_ARG_KEY]
 */
class TransferConfirmationViewModel constructor(
    private val _state: SavedStateHandle
) : ViewModel() {

    companion object {
        // argument keys correspond to the keys used in the navigation graph
        const val SAVINGS_GOAL_ARG_KEY = "savingsGoal"
        const val ROUND_UP_AMOUNT_ARG_KEY = "roundUpAmount"
        const val ACCOUNT_ID_ARG_KEY = "accountId"
        const val ACCOUNT_CURRENCY_ARG_KEY = "accountCurrency"

        var SavedStateHandle.accountCurrencyArg: Currency
            get() = this[ACCOUNT_CURRENCY_ARG_KEY] ?: throw IllegalArgumentException(ACCOUNT_CURRENCY_ARG_KEY)
            set(value) { this[ACCOUNT_CURRENCY_ARG_KEY] = value}

        var SavedStateHandle.savingsGoalArg: SavingsGoal
            get() = this[SAVINGS_GOAL_ARG_KEY] ?: throw IllegalArgumentException(SAVINGS_GOAL_ARG_KEY)
            set(value) { this[SAVINGS_GOAL_ARG_KEY] = value}

        var SavedStateHandle.roundUpAmountArg: BigDecimal
            get() = this[ROUND_UP_AMOUNT_ARG_KEY] ?: throw IllegalArgumentException(ROUND_UP_AMOUNT_ARG_KEY)
            set(value) { this[ROUND_UP_AMOUNT_ARG_KEY] = value}

        var SavedStateHandle.accountIdArg: String
            get() = this[ACCOUNT_ID_ARG_KEY] ?: throw IllegalArgumentException(ACCOUNT_ID_ARG_KEY)
            set(value) { this[ACCOUNT_ID_ARG_KEY] = value}
    }

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
            _state.roundUpAmountArg,
            _state.accountCurrencyArg.currencyCode,
            _locale
        )
        _info.value = _stringSupplier.get(R.string.transferInfo)
             .format(amountFormatted, _state.savingsGoalArg.name)
    }

    fun onConfirmCommand() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _interactor.execute(
                    _state.accountIdArg,
                    _state.savingsGoalArg.id,
                    _state.accountCurrencyArg,
                    _state.roundUpAmountArg
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
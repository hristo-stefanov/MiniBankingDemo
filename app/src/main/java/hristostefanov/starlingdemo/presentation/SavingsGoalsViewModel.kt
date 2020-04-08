package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.*
import androidx.navigation.NavDirections
import hristostefanov.starlingdemo.NavGraphXmlDirections
import hristostefanov.starlingdemo.business.dependences.ServiceException
import hristostefanov.starlingdemo.business.entities.SavingsGoal
import hristostefanov.starlingdemo.business.interactors.ListSavingGoalsInteractor
import hristostefanov.starlingdemo.ui.SavingsGoalsFragmentDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

/**
 * Expected arguments passed through [SavedStateHandle]:
 * [ACCOUNT_ID_ARG_KEY], [ACCOUNT_CURRENCY_ARG_KEY] and [ROUND_UP_AMOUNT_ARG_KEY]
 */
class SavingsGoalsViewModel @Inject constructor(
    private val _state: SavedStateHandle
) : ViewModel() {

    companion object {
        // argument keys correspond to the keys used in the navigation graph
        const val ROUND_UP_AMOUNT_ARG_KEY = "roundUpAmount"
        const val ACCOUNT_ID_ARG_KEY = "accountId"
        const val ACCOUNT_CURRENCY_ARG_KEY = "accountCurrency"

        var SavedStateHandle.accountCurrencyArg: Currency
            get() = this[ACCOUNT_CURRENCY_ARG_KEY] ?: throw IllegalArgumentException(ACCOUNT_CURRENCY_ARG_KEY)
            set(value) { this[ACCOUNT_CURRENCY_ARG_KEY] = value}

        var SavedStateHandle.roundUpAmountArg: BigDecimal
            get() = this[ROUND_UP_AMOUNT_ARG_KEY] ?: throw IllegalArgumentException(ROUND_UP_AMOUNT_ARG_KEY)
            set(value) { this[ROUND_UP_AMOUNT_ARG_KEY] = value}

        var SavedStateHandle.accountIdArg: String
            get() = this[ACCOUNT_ID_ARG_KEY] ?: throw IllegalArgumentException(ACCOUNT_ID_ARG_KEY)
            set(value) { this[ACCOUNT_ID_ARG_KEY] = value}
    }

    @Inject
    internal lateinit var listSavingGoalsInteractor: ListSavingGoalsInteractor

    private var _goals: List<SavingsGoal> = emptyList()

    private val _navigationChannel = Channel<NavDirections>()
    val navigationChannel: ReceiveChannel<NavDirections> = _navigationChannel

    private val _list = MutableLiveData<List<DisplaySavingsGoal>>()
    val list: LiveData<List<DisplaySavingsGoal>> = _list

    init {
        viewModelScope.launch {
            try {
                _goals = withContext(Dispatchers.IO) {
                    listSavingGoalsInteractor.execute(_state.accountIdArg)
                }
                _list.value = _goals.map { DisplaySavingsGoal(it.name) }
            } catch (e: ServiceException) {
                e.localizedMessage?.also {
                    _navigationChannel.send(NavGraphXmlDirections.toErrorDialog(it))
                }
            }
        }
    }


    fun onSavingsGoalClicked(position: Int) {
        _goals.getOrNull(position)?.also {
            viewModelScope.launch {
                _navigationChannel.send(
                    SavingsGoalsFragmentDirections.actionToTransferConfirmationDestination(
                        it,
                        _state.roundUpAmountArg,
                        _state.accountCurrencyArg,
                        _state.accountIdArg
                    )
                )
            }
        }
    }

    fun onAddSavingsGoalCommand() {
        viewModelScope.launch {
            _navigationChannel.send(
                SavingsGoalsFragmentDirections.actionToCreateSavingsGoalDestination(
                    _state.accountIdArg,
                    _state.accountCurrencyArg,
                    _state.roundUpAmountArg
                )
            )
        }
    }
}
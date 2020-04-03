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

class SavingsGoalsViewModel @Inject constructor(
    private val _savedSateHandle: SavedStateHandle
) : ViewModel() {
    @Inject
    internal lateinit var listSavingGoalsInteractor: ListSavingGoalsInteractor

    private var _goals: List<SavingsGoal> = emptyList()

    private val _navigationChannel = Channel<NavDirections>()
    val navigationChannel: ReceiveChannel<NavDirections> = _navigationChannel

    private val _list = MutableLiveData<List<DisplaySavingsGoal>>()
    val list: LiveData<List<DisplaySavingsGoal>> = _list

    private val _accountId: String = _savedSateHandle[ACCOUNT_ID_KEY]
        ?: throw IllegalArgumentException(ACCOUNT_ID_KEY)
    private val _roundUpAmount: BigDecimal = _savedSateHandle[ROUND_UP_AMOUNT_KEY]
        ?: throw IllegalArgumentException(ROUND_UP_AMOUNT_KEY)
    private val _accountCurrency: Currency = _savedSateHandle[ACCOUNT_CURRENCY_KEY]
        ?: throw IllegalArgumentException(ACCOUNT_CURRENCY_KEY)

    init {
        viewModelScope.launch {
            try {
                _goals = withContext(Dispatchers.IO) {
                    listSavingGoalsInteractor.execute(_accountId)
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
                        _roundUpAmount,
                        _accountCurrency,
                        _accountId
                    )
                )
            }
        }
    }

    fun onAddSavingsGoalCommand() {
        viewModelScope.launch {
            _navigationChannel.send(
                SavingsGoalsFragmentDirections.actionToCreateSavingsGoalDestination(
                    _accountId,
                    _accountCurrency,
                    _roundUpAmount
                )
            )
        }
    }
}
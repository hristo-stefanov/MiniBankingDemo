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
import javax.inject.Inject

/**
 * Expected arguments passed through [SavedStateHandle]:
 * [ACCOUNT_ID_KEY], [ACCOUNT_CURRENCY_KEY] and [ROUND_UP_AMOUNT_KEY]
 */
class SavingsGoalsViewModel @Inject constructor(
    private val _state: SavedStateHandle
) : ViewModel() {
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
                    listSavingGoalsInteractor.execute(_state.accountId)
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
                        _state.roundUpAmount,
                        _state.accountCurrency,
                        _state.accountId
                    )
                )
            }
        }
    }

    fun onAddSavingsGoalCommand() {
        viewModelScope.launch {
            _navigationChannel.send(
                SavingsGoalsFragmentDirections.actionToCreateSavingsGoalDestination(
                    _state.accountId,
                    _state.accountCurrency,
                    _state.roundUpAmount
                )
            )
        }
    }
}
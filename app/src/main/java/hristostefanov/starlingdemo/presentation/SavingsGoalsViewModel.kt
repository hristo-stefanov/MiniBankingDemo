package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import hristostefanov.starlingdemo.NavGraphXmlDirections
import hristostefanov.starlingdemo.business.dependences.ServiceException
import hristostefanov.starlingdemo.business.entities.SavingsGoal
import hristostefanov.starlingdemo.business.interactors.ListSavingGoalsInteractor
import hristostefanov.starlingdemo.ui.SavingsGoalsFragmentArgs
import hristostefanov.starlingdemo.ui.SavingsGoalsFragmentDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SavingsGoalsViewModel @Inject constructor(
    private val _args: SavingsGoalsFragmentArgs
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
                    listSavingGoalsInteractor.execute(_args.accountId)
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
                        _args.roundUpAmount,
                        _args.accountCurrency,
                        _args.accountId
                    )
                )
            }
        }
    }

    fun onAddSavingsGoalCommand() {
        viewModelScope.launch {
            _navigationChannel.send(
                SavingsGoalsFragmentDirections.actionToCreateSavingsGoalDestination(
                    _args.accountId,
                    _args.accountCurrency,
                    _args.roundUpAmount
                )
            )
        }
    }
}
package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hristostefanov.starlingdemo.NavGraphXmlDirections
import hristostefanov.starlingdemo.business.dependences.ServiceException
import hristostefanov.starlingdemo.business.entities.SavingsGoal
import hristostefanov.starlingdemo.business.interactors.DataSourceChangedEvent
import hristostefanov.starlingdemo.business.interactors.ListSavingGoalsInteractor
import hristostefanov.starlingdemo.ui.SavingsGoalsFragmentArgs
import hristostefanov.starlingdemo.ui.SavingsGoalsFragmentDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class SavingsGoalsViewModel @Inject constructor(
    private val _args: SavingsGoalsFragmentArgs
) : ViewModel() {

    @Inject
    internal lateinit var eventBus: EventBus

    @Inject
    internal lateinit var listSavingGoalsInteractor: ListSavingGoalsInteractor

    private var _goals: List<SavingsGoal> = emptyList()

    private val _list = MutableLiveData<List<DisplaySavingsGoal>>()
    val list: LiveData<List<DisplaySavingsGoal>> = _list

    @Inject
    fun init() {
        load()
        eventBus.register(this)
    }

    override fun onCleared() {
        eventBus.unregister(this)
        super.onCleared()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDataSourceChanged(event: DataSourceChangedEvent) {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            try {
                _goals = withContext(Dispatchers.IO) {
                    listSavingGoalsInteractor.execute(_args.accountId)
                }
                _list.value = _goals.map { DisplaySavingsGoal(it.name) }
            } catch (e: ServiceException) {
                e.localizedMessage?.also {
                    eventBus.post(Navigation.Forward(NavGraphXmlDirections.toErrorDialog(it)))
                }
            }
        }
    }


    fun onSavingsGoalClicked(position: Int) {
        _goals.getOrNull(position)?.also {
            eventBus.post(
                Navigation.Forward(
                    SavingsGoalsFragmentDirections.actionToTransferConfirmationDestination(
                        it,
                        _args.roundUpAmount,
                        _args.accountCurrency,
                        _args.accountId
                    )
                )
            )
        }
    }

    fun onAddSavingsGoalCommand() {
        eventBus.post(
            Navigation.Forward(
                SavingsGoalsFragmentDirections.actionToCreateSavingsGoalDestination(
                    _args.accountId,
                    _args.accountCurrency
                )
            )
        )
    }
}
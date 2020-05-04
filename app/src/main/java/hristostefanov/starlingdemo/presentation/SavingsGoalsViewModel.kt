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
import hristostefanov.starlingdemo.util.NavigationChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class SavingsGoalsViewModel @Inject constructor(
    private val args: SavingsGoalsFragmentArgs
) : ViewModel() {

    @Inject
    internal lateinit var eventBus: EventBus

    @Inject
    internal lateinit var listSavingGoalsInteractor: ListSavingGoalsInteractor

    @Inject
    @NavigationChannel
    internal lateinit var navigationChannel: Channel<Navigation>

    private var goals: List<SavingsGoal> = emptyList()

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
                goals = withContext(Dispatchers.IO) {
                    listSavingGoalsInteractor.execute(args.accountId)
                }
                _list.value = goals.map { DisplaySavingsGoal(it.name) }
            } catch (e: ServiceException) {
                e.localizedMessage?.also {
                    navigationChannel.send(Navigation.Forward(NavGraphXmlDirections.toErrorDialog(it)))
                }
            }
        }
    }


    fun onSavingsGoalClicked(position: Int) {
        goals.getOrNull(position)?.also {
            viewModelScope.launch {
                navigationChannel.send(
                    Navigation.Forward(
                        SavingsGoalsFragmentDirections.actionToTransferConfirmationDestination(
                            it,
                            args.roundUpAmount,
                            args.accountCurrency,
                            args.accountId
                        )
                    )
                )
            }
        }
    }

    fun onAddSavingsGoalCommand() {
        viewModelScope.launch {
            navigationChannel.send(
                Navigation.Forward(
                    SavingsGoalsFragmentDirections.actionToCreateSavingsGoalDestination(
                        args.accountId,
                        args.accountCurrency
                    )
                )
            )
        }
    }
}
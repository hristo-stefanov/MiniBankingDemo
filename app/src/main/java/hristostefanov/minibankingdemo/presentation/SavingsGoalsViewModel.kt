package hristostefanov.minibankingdemo.presentation

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.NavGraphXmlDirections
import hristostefanov.minibankingdemo.business.dependences.ServiceException
import hristostefanov.minibankingdemo.business.entities.SavingsGoal
import hristostefanov.minibankingdemo.business.interactors.DataSourceChangedEvent
import hristostefanov.minibankingdemo.ui.SavingsGoalsFragmentArgs
import hristostefanov.minibankingdemo.ui.SavingsGoalsFragmentDirections
import hristostefanov.minibankingdemo.util.SessionRegistry
import hristostefanov.minibankingdemo.util.NavigationChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@HiltViewModel
class SavingsGoalsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sessionRegistry: SessionRegistry,
    private val eventBus: EventBus,
    @NavigationChannel
    private val navigationChannel: Channel<Navigation>
) : ViewModel() {

    private val args = SavingsGoalsFragmentArgs.fromSavedStateHandle(savedStateHandle)

    private var goals: List<SavingsGoal> = emptyList()

    private val _list = MutableLiveData<List<DisplaySavingsGoal>>()
    val list: LiveData<List<DisplaySavingsGoal>> = _list

    init {
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
                goals = sessionRegistry.sessionComponent?.listSavingGoalInteractor?.execute(args.accountId)
                    ?: emptyList()
                _list.value = goals.map { DisplaySavingsGoal(it.id, it.name) }
            } catch (e: ServiceException) {
                e.localizedMessage?.also {
                    navigationChannel.send(Navigation.Forward(NavGraphXmlDirections.toErrorDialog(it)))
                }
            }
        }
    }


    fun onSavingsGoalClicked(savingsGoalId: String) {
        goals
            .find { it.id == savingsGoalId }
            ?.also {
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
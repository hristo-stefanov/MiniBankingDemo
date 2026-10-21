package hristostefanov.minibankingdemo.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.business.entities.SavingsGoal
import hristostefanov.minibankingdemo.ui.SavingsGoalsFragmentDirections
import hristostefanov.minibankingdemo.util.DataSourceChangedEvent
import hristostefanov.minibankingdemo.util.Session
import hristostefanov.minibankingdemo.util.MainCommandChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@HiltViewModel
class SavingsGoalsViewModel @Inject constructor(
    private val session: Session,
    private val eventBus: EventBus,
    @MainCommandChannel
    private val mainCommandChannel: Channel<MainCommand>
) : ViewModel() {

    private val _list = MutableLiveData<List<SavingsGoal>>()
    val list: LiveData<List<SavingsGoal>> = _list

    init {
        load()
        eventBus.register(this)
    }

    override fun onCleared() {
        eventBus.unregister(this)
        super.onCleared()
    }

    // TODO
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDataSourceChanged(event: DataSourceChangedEvent) {
        load()
    }

    private fun load() {
        _list.value = session.state.value.selectedAccount?.savingsGoals ?: emptyList()
    }

    fun onSavingsGoalClicked(savingsGoalId: String) {
        session.updateState { it.copy(selectedSavingsGoalId = savingsGoalId) }
        viewModelScope.launch {
            mainCommandChannel.send(
                MainCommand.NavigateForward(
                    SavingsGoalsFragmentDirections.actionToTransferConfirmationDestination()
                )
            )
        }
    }

    fun onAddSavingsGoalCommand() {
        viewModelScope.launch {
            mainCommandChannel.send(
                MainCommand.NavigateForward(
                    SavingsGoalsFragmentDirections.actionToCreateSavingsGoalDestination()
                )
            )
        }
    }
}
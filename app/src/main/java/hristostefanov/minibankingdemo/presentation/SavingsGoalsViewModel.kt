package hristostefanov.minibankingdemo.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.business.interactors.DataSourceChangedEvent
import hristostefanov.minibankingdemo.ui.SavingsGoalsFragmentArgs
import hristostefanov.minibankingdemo.ui.SavingsGoalsFragmentDirections
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import hristostefanov.minibankingdemo.util.MainCommandChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@HiltViewModel
class SavingsGoalsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loginSessionRegistry: LoginSessionRegistry,
    private val eventBus: EventBus,
    @MainCommandChannel
    private val mainCommandChannel: Channel<MainCommand>
) : ViewModel() {

    private val args = SavingsGoalsFragmentArgs.fromSavedStateHandle(savedStateHandle)

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

    // TODO
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDataSourceChanged(event: DataSourceChangedEvent) {
        load()
    }

    private fun load() {
        _list.value = args.savingsGoals.asList()
    }

    fun onSavingsGoalClicked(savingsGoalId: String, savingsGoalName: String) {
        with(loginSessionRegistry.requireComponent.data) {
            this.savingsGoalId = savingsGoalId
            this.savingsGoalName = savingsGoalName

            viewModelScope.launch {
                mainCommandChannel.send(
                    MainCommand.NavigateForward(
                        SavingsGoalsFragmentDirections.actionToTransferConfirmationDestination(
                            savingsGoalName = savingsGoalName,
                            roundUpAmount = selectedAccount.roundUp,
                            accountCurrency = selectedAccount.currency,
                        )
                    )
                )
            }
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
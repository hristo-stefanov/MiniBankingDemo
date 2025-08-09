package hristostefanov.minibankingdemo.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.business.interactors.DataSourceChangedEvent
import hristostefanov.minibankingdemo.ui.SavingsGoalsFragmentArgs
import hristostefanov.minibankingdemo.usecase.Continuation
import hristostefanov.minibankingdemo.usecase.ContinuationId
import hristostefanov.minibankingdemo.usecase.CreateSavingsGoal
import hristostefanov.minibankingdemo.usecase.Trigger
import hristostefanov.minibankingdemo.util.ContinuationChannel
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import hristostefanov.minibankingdemo.util.TriggerChannel
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
    private val userInterface: UserInterfaceImpl,
    private val eventBus: EventBus,
    @ContinuationChannel
    private val continuationChannel: Channel<Continuation>,
    @TriggerChannel
    private val triggerChannel: Channel<Trigger>
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDataSourceChanged(event: DataSourceChangedEvent) {
        load()
    }

    private fun load() {
        _list.value = args.savingsGoals.asList()
    }

    fun onSavingsGoalClicked(savingsGoalId: String, savingsGoalName: String) {
        viewModelScope.launch {
            continuationChannel.send(
                Continuation(
                    // TODO shouldn't we get the continuation is as an argument instead of
                    // hardcoding it?
                    ContinuationId.TransferRoundUp_SavingsGoalSelected,
                    listOf(savingsGoalId, savingsGoalName)
                )
            )
        }
    }

    fun onAddSavingsGoalCommand() {
        viewModelScope.launch {
            triggerChannel.send(CreateSavingsGoal)
        }
    }
}
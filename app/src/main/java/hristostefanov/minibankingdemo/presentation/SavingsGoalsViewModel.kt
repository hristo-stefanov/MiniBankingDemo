package hristostefanov.minibankingdemo.presentation

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.business.entities.SavingsGoal
import hristostefanov.minibankingdemo.business.interactors.DataSourceChangedEvent
import hristostefanov.minibankingdemo.ui.SavingsGoalsFragmentArgs
import hristostefanov.minibankingdemo.usecase.Continuation
import hristostefanov.minibankingdemo.usecase.ContinuationId
import hristostefanov.minibankingdemo.util.ContinuationChannel
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
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
    private val loginSessionRegistry: LoginSessionRegistry,
    private val eventBus: EventBus,
    @NavigationChannel
    private val navigationChannel: Channel<Navigation>,
    @ContinuationChannel
    private val continuationChannel: Channel<Continuation>
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
        _list.value = args.savingsGoals.asList()
    }

    fun onSavingsGoalClicked(savingsGoalId: String) {
        viewModelScope.launch {
            continuationChannel.send(
                Continuation(
                    // TODO shouldn't we get the continuation is as an argument instead of
                    // hardcoding it?
                    ContinuationId.TransferRoundUp_SavingsGoalSelected,
                    savingsGoalId
                )
            )
        }
    }

    fun onAddSavingsGoalCommand() {
        viewModelScope.launch {
//            navigationChannel.send(
//                Navigation.Forward(
//                    SavingsGoalsFragmentDirections.actionToCreateSavingsGoalDestination(
//                        args.accountId,
//                        args.accountCurrency
//                    )
//                )
//            )
        }
    }
}
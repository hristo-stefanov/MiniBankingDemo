package hristostefanov.minibankingdemo.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.usecase.input.CreateSavingsGoalInteractor
import hristostefanov.minibankingdemo.usecase.input.isFailure
import hristostefanov.minibankingdemo.util.Session
import hristostefanov.minibankingdemo.util.MainCommandChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateSavingsGoalViewModel @Inject constructor(
    private val createSavingsGoalInteractor: CreateSavingsGoalInteractor,
    private val session: Session,
    @MainCommandChannel
    private val mainCommandChannel: Channel<MainCommand>,
    private val mainUI: MainUI,
) : ViewModel() {

    // exposing MutableLiveData to allow two-way data binding
    val name: MutableLiveData<String> = MutableLiveData<String>()

    val createCommandEnabled: LiveData<Boolean> by lazy {
        name.map { name ->
            createSavingsGoalInteractor.validateName(name)
        }
    }

    fun onCreateCommand() {
        name.value?.let { name ->
            viewModelScope.launch {
                session.state.value.selectedAccount?.let { account ->
                    val status = createSavingsGoalInteractor(
                        goalName = name,
                        accountId = account.accountId,
                        accountCurrency = account.currency
                    )

                    if (status.isFailure()) {
                        mainUI.presentStatus(status)
                    } else {
                        mainCommandChannel.send(MainCommand.NavigateBackward)
                    }
                }
            }
        }
    }
}
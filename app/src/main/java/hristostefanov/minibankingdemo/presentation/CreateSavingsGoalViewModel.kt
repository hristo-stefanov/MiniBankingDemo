package hristostefanov.minibankingdemo.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.usecase.CreateSavingsGoalInteractor
import hristostefanov.minibankingdemo.usecase.Outcome
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import hristostefanov.minibankingdemo.util.NavigationChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class CreateSavingsGoalViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val createSavingsGoalInteractor: CreateSavingsGoalInteractor,
    private val userInterface: UserInterface,
    private val loginSessionRegistry: LoginSessionRegistry,
    @NavigationChannel
    private val navigationChannel: Channel<Navigation>
) : ViewModel() {

    // Another approach could be using @EntryPoint, see
    // https://medium.com/androiddevelopers/hilt-adding-components-to-the-hierarchy-96f207d6d92d

    companion object {
        const val NAME_KEY = "name"
    }

    // exposing MutableLiveData to allow two-way data binding
    val name: MutableLiveData<String> = savedState.getLiveData(NAME_KEY)

    // TODO validation rule
    private fun validateName(name: String) = name.isNotBlank()

    open val createCommandEnabled: LiveData<Boolean> by lazy {
        savedState.getLiveData<String>(NAME_KEY).map { name ->
            validateName(name) ?: false
            true
        }
    }

    open fun onCreateCommand() {
        savedState.get<String>(NAME_KEY)?.also { name ->
            viewModelScope.launch {
                with(loginSessionRegistry.requireComponent.data) {
                    val outcome = createSavingsGoalInteractor.start(
                        userInterface = userInterface,
                        goalName = name,
                        accountId = selectedAccount.accountId,
                        accountCurrency = selectedAccount.currency
                    )
                    if (outcome is Outcome.Completed<*>) {
                        navigationChannel.send(Navigation.Backward)
                    }
                }
            }
        }
    }
}
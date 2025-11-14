package hristostefanov.minibankingdemo.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.usecase.input.CreateSavingsGoalInteractor
import hristostefanov.minibankingdemo.usecase.input.isFailure
import hristostefanov.minibankingdemo.usecase.output.CommonUI
import hristostefanov.minibankingdemo.util.LoginSessionData
import hristostefanov.minibankingdemo.util.NavigationChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class CreateSavingsGoalViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val createSavingsGoalInteractor: CreateSavingsGoalInteractor,
    @NavigationChannel
    private val navigationChannel: Channel<Navigation>,
    private val commonUI: CommonUI,
    private val statusUI: StatusUI,
    private val loginSessionData: LoginSessionData
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
                loginSessionData.selectedAccount?.let { selectedAccount ->
                    val status = createSavingsGoalInteractor(
                        goalName = name,
                        accountId = selectedAccount.accountId,
                        accountCurrency = selectedAccount.currency
                    )

                    if (status.isFailure()) {
                        statusUI.presentStatus(status)
                    } else {
                        navigationChannel.send(Navigation.Backward)
                    }
                } ?: throw IllegalStateException("No selected account")
            }
        }
    }
}
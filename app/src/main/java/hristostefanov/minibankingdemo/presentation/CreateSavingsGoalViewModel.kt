package hristostefanov.minibankingdemo.presentation

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.business.dependences.ServiceException
import hristostefanov.minibankingdemo.ui.CreateSavingsGoalFragmentArgs
import hristostefanov.minibankingdemo.ui.CreateSavingsGoalFragmentDirections
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import hristostefanov.minibankingdemo.util.NavigationChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class CreateSavingsGoalViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val loginSessionRegistry: LoginSessionRegistry,
    @NavigationChannel
    private val navigationChannel: Channel<Navigation>
) : ViewModel() {

    private val args = CreateSavingsGoalFragmentArgs.fromSavedStateHandle(savedState)
    // Another approach could be using @EntryPoint, see
    // https://medium.com/androiddevelopers/hilt-adding-components-to-the-hierarchy-96f207d6d92d

    companion object {
        const val NAME_KEY = "name"
    }

    // exposing MutableLiveData to allow two-way data binding
    val name: MutableLiveData<String> = savedState.getLiveData(NAME_KEY)

    open val createCommandEnabled: LiveData<Boolean> by lazy {
        Transformations.map(savedState.getLiveData<String>(NAME_KEY)) { name ->
            loginSessionRegistry.component?.createSavingGoalsInteractor?.validateName(name) ?: false
        }
    }

    open fun onCreateCommand() {
        savedState.get<String>(NAME_KEY)?.also { name ->
            if (loginSessionRegistry.component?.createSavingGoalsInteractor?.validateName(name) == true) {
                viewModelScope.launch {
                    try {
                        loginSessionRegistry?.component?.createSavingGoalsInteractor?.execute(
                            name,
                            args.accountId,
                            args.accountCurrency
                        )
                        navigationChannel.send(Navigation.Backward)
                    } catch (e: ServiceException) {
                        e.localizedMessage?.also {
                            navigationChannel.send(
                                Navigation.Forward(
                                    CreateSavingsGoalFragmentDirections.toErrorDialog(
                                        it
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
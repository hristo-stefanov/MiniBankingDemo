package hristostefanov.minibankingdemo.presentation

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.business.dependences.ServiceException
import hristostefanov.minibankingdemo.ui.CreateSavingsGoalFragmentArgs
import hristostefanov.minibankingdemo.ui.CreateSavingsGoalFragmentDirections
import hristostefanov.minibankingdemo.util.NavigationChannel
import hristostefanov.minibankingdemo.util.SessionRegistry
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class CreateSavingsGoalViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val sessionRegistry: SessionRegistry,
    @NavigationChannel
    private val navigationChannel: Channel<Navigation>
) : ViewModel() {

    private val args = CreateSavingsGoalFragmentArgs.fromSavedStateHandle(savedState)
    // TODO look for other options to inject the viewmodel from SessionComponent directly, see https://dagger.dev/hilt/view-model.html
    private val createSavingsGoalInteractor = sessionRegistry.sessionComponent.createSavingGoalsInteractor

    companion object {
        internal const val NAME_KEY = "name"

        private var SavedStateHandle.name: String
            get() = this[NAME_KEY] ?: ""
            set(value) {
                this[NAME_KEY] = value
            }
    }

    // lazy to avoid initializing before savedState is provided by the init() method
    // exposing MutableLiveData to allow two-way data binding
    val name: MutableLiveData<String> by lazy {
        savedState.getLiveData(NAME_KEY)
    }

    open val createCommandEnabled: LiveData<Boolean> by lazy {
        Transformations.map(savedState.getLiveData<String>(NAME_KEY)) { name ->
            createSavingsGoalInteractor.validateName(name)
        }
    }


    open fun onCreateCommand() {
        savedState.name.also { name ->
            if (createSavingsGoalInteractor.validateName(name)) {
                viewModelScope.launch {
                    try {
                        createSavingsGoalInteractor.execute(
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
package hristostefanov.minibankingdemo.presentation

import androidx.lifecycle.*
import hristostefanov.minibankingdemo.business.dependences.ServiceException
import hristostefanov.minibankingdemo.business.interactors.CreateSavingsGoalInteractor
import hristostefanov.minibankingdemo.ui.CreateSavingsGoalFragmentArgs
import hristostefanov.minibankingdemo.ui.CreateSavingsGoalFragmentDirections
import hristostefanov.minibankingdemo.util.NavigationChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import javax.inject.Inject

open class CreateSavingsGoalViewModel
@Inject
constructor(
    private val createSavingsGoalInteractor: CreateSavingsGoalInteractor,
    @NavigationChannel private val navigationChannel: Channel<Navigation>
) : ViewModel() {

    private lateinit var args: CreateSavingsGoalFragmentArgs
    private lateinit var savedState: SavedStateHandle
    private var isInitialized = false

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

    fun init(args: CreateSavingsGoalFragmentArgs, savedState: SavedStateHandle) {
        if (isInitialized) throw IllegalStateException()
        this.args = args
        this.savedState = savedState
        isInitialized = true
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
package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.*
import hristostefanov.starlingdemo.business.dependences.ServiceException
import hristostefanov.starlingdemo.business.interactors.CreateSavingsGoalInteractor
import hristostefanov.starlingdemo.ui.CreateSavingsGoalFragmentArgs
import hristostefanov.starlingdemo.ui.CreateSavingsGoalFragmentDirections
import hristostefanov.starlingdemo.util.NavigationChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

open class CreateSavingsGoalViewModel
constructor(
    private val args: CreateSavingsGoalFragmentArgs,
    private val savedState: SavedStateHandle
) : ViewModel() {

    companion object {
        internal const val NAME_KEY = "name"

        private var SavedStateHandle.name: String
            get() = this[NAME_KEY] ?: ""
            set(value) {
                this[NAME_KEY] = value
            }
    }

    @Inject
    internal lateinit var createSavingsGoalInteractor: CreateSavingsGoalInteractor

    @Inject
    @NavigationChannel
    internal lateinit var navigationChannel: Channel<Navigation>

    open fun onNameChanged(name: String) {
        savedState.name = name
    }

    open val createCommand: Command = object : Command {
        override val enabledLive: LiveData<Boolean> = Transformations.map(savedState.getLiveData<String>(NAME_KEY)) { name ->
            createSavingsGoalInteractor.validateName(name)
        }

        override fun execute() {
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
}
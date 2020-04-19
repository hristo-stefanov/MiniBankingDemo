package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hristostefanov.starlingdemo.business.dependences.ServiceException
import hristostefanov.starlingdemo.business.interactors.CreateSavingsGoalInteractor
import hristostefanov.starlingdemo.ui.CreateSavingsGoalFragmentArgs
import hristostefanov.starlingdemo.ui.CreateSavingsGoalFragmentDirections
import hristostefanov.starlingdemo.util.NavigationChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.util.function.Consumer
import java.util.function.Predicate
import javax.inject.Inject

open class CreateSavingsGoalViewModel

constructor(
    private val _args: CreateSavingsGoalFragmentArgs,
    private val _state: SavedStateHandle
) : ViewModel() {

    companion object {
        const val NAME_KEY = "name"

        var SavedStateHandle.name: String
            get() = this[NAME_KEY] ?: throw IllegalArgumentException(NAME_KEY)
            set(value) {
                this[NAME_KEY] = value
            }
    }

    @Inject
    internal lateinit var createSavingsGoalInteractor: CreateSavingsGoalInteractor
    @Inject @NavigationChannel
    internal lateinit var navigationChannel: Channel<Navigation>

    open fun onNameChanged(name: String) {
        _state.name = name
    }

    open val createCommand: Command = CommandImpl(
        _state,
        Predicate { state -> createSavingsGoalInteractor.validateName(state.name) },
        listOf(NAME_KEY),
        Consumer {state ->
            viewModelScope.launch {
                try {
                    createSavingsGoalInteractor.execute(
                        state.name,
                        _args.accountId,
                        _args.accountCurrency
                    )

                    navigationChannel.send(Navigation.Backward)
                } catch (e: ServiceException) {
                    e.localizedMessage?.also {
                        navigationChannel.send(Navigation.Forward(CreateSavingsGoalFragmentDirections.toErrorDialog(it)))
                    }
                }
            }
        })
}


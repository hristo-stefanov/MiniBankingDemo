package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import hristostefanov.starlingdemo.NavGraphXmlDirections
import hristostefanov.starlingdemo.business.dependences.ServiceException
import hristostefanov.starlingdemo.business.interactors.CreateSavingsGoalInteractor
import hristostefanov.starlingdemo.ui.CreateSavingsGoalFragmentArgs
import hristostefanov.starlingdemo.ui.CreateSavingsGoalFragmentDirections
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import java.util.*
import java.util.function.Consumer
import java.util.function.Predicate
import javax.inject.Inject

class CreateSavingsGoalViewModel

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

    private val _navigationChannel = Channel<NavDirections>()
    val navigationChannel: ReceiveChannel<NavDirections> = _navigationChannel

    fun onNameChanged(name: String) {
        _state.name = name
    }

    val createCommand: Command = CommandImpl(
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

                    // TODO consider navigating UP instead
                    _navigationChannel.send(
                        CreateSavingsGoalFragmentDirections.actionToSavingsGoalsDestination(
                            _args.accountId,
                            _args.accountCurrency,
                            _args.roundUpAmount
                        )
                    )
                } catch (e: ServiceException) {
                    e.localizedMessage?.also {
                        _navigationChannel.send(NavGraphXmlDirections.toErrorDialog(it))
                    }
                }
            }
        })
}


package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import hristostefanov.starlingdemo.NavGraphXmlDirections
import hristostefanov.starlingdemo.business.dependences.ServiceException
import hristostefanov.starlingdemo.business.interactors.CreateSavingsGoalInteractor
import hristostefanov.starlingdemo.ui.CreateSavingsGoalFragmentDirections
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.util.function.Consumer
import java.util.function.Predicate
import javax.inject.Inject

class CreateSavingsGoalViewModel
/**
 * Expected arguments passed through [SavedStateHandle]:
 * [ACCOUNT_CURRENCY_KEY], [ACCOUNT_ID_KEY] and [ROUND_UP_AMOUNT_KEY]
 */
constructor(
    private val _state: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val NAME_KEY = "name"

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

    val createCommand: ICmd = Cmd(
        _state,
        Predicate { state -> createSavingsGoalInteractor.validateName(state.name) },
        listOf(NAME_KEY),
        Consumer {
            viewModelScope.launch {
                try {
                    createSavingsGoalInteractor.execute(
                        _state.name,
                        _state.accountId,
                        _state.accountCurrency
                    )

                    // TODO consider navigating UP instead
                    _navigationChannel.send(
                        CreateSavingsGoalFragmentDirections.actionToSavingsGoalsDestination(
                            _state.accountId,
                            _state.accountCurrency,
                            _state.roundUpAmount
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


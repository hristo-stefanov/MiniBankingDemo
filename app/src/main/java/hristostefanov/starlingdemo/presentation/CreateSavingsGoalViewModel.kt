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
import java.util.function.Consumer
import java.util.function.Predicate
import javax.inject.Inject

class CreateSavingsGoalViewModel
/**
 * Expected arguments [ACCOUNT_CURRENCY_KEY], [ACCOUNT_ID_KEY] and [ROUND_UP_AMOUNT_KEY]
 */
constructor(
    private val _savedStateHandle: SavedStateHandle
) : ViewModel() {
    @Inject
    internal lateinit var createSavingsGoalInteractor: CreateSavingsGoalInteractor

    private val _navigationChannel = Channel<NavDirections>()
    val navigationChannel: ReceiveChannel<NavDirections> = _navigationChannel

    fun onNameChanged(name: String) {
        _savedStateHandle.name = name
    }

    val createCommand: ICmd = Cmd(
        _savedStateHandle,
        Predicate { createSavingsGoalInteractor.validateName(it.name) },
        listOf(NAME_KEY),
        Consumer {
            viewModelScope.launch {
                try {
                    createSavingsGoalInteractor.execute(
                        _savedStateHandle.name,
                        _savedStateHandle.accountId,
                        _savedStateHandle.currency
                    )

                    // TODO consider navigating UP instead
                    _navigationChannel.send(
                        CreateSavingsGoalFragmentDirections.actionToSavingsGoalsDestination(
                            _savedStateHandle.accountId,
                            _savedStateHandle.currency,
                            _savedStateHandle.roundUpAmount
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

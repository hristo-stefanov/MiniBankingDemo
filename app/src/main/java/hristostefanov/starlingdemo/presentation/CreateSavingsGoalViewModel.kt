package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.*
import androidx.navigation.NavDirections
import hristostefanov.starlingdemo.NavGraphXmlDirections
import hristostefanov.starlingdemo.business.dependences.ServiceException
import hristostefanov.starlingdemo.business.interactors.CreateSavingsGoalInteractor
import hristostefanov.starlingdemo.ui.CreateSavingsGoalFragmentDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import javax.inject.Inject

class CreateSavingsGoalViewModel constructor(
    private val _savedStateHandle: SavedStateHandle
) : ViewModel() {
    @Inject
    internal lateinit var createSavingsGoalInteractor: CreateSavingsGoalInteractor

    private val _navigationChannel = Channel<NavDirections>()
    val navigationChannel: ReceiveChannel<NavDirections> = _navigationChannel

    private val _createCommandEnabled = Transformations.map(_savedStateHandle.getLiveData("name", "")) {
        it.isNotBlank()
    }
    val createCommandEnabled: LiveData<Boolean> = _createCommandEnabled

    fun onNameChanged(name: String) {
        _savedStateHandle["name"] = name
    }

    fun onCreateCommand() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                createSavingsGoalInteractor.execute(
                    _savedStateHandle["name"]!!,
                    _savedStateHandle["accountId"]!!,
                    _savedStateHandle["accountCurrency"]!!
                )
                // TODO consider navigating UP instead
                _navigationChannel.send(CreateSavingsGoalFragmentDirections.actionToSavingsGoalsDestination(
                    _savedStateHandle["accountId"]!!,
                    _savedStateHandle["accountCurrency"]!!,
                    _savedStateHandle["roundUpAmount"]!!
                ))
            } catch (e: ServiceException) {
                e.localizedMessage?.also {
                    _navigationChannel.send(NavGraphXmlDirections.toErrorDialog(it))
                }
            }
        }
    }
}

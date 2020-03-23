package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class CreateSavingsGoalViewModel @Inject constructor(
    private val createSavingsGoalInteractor: CreateSavingsGoalInteractor,
    private val sessionState: SessionState
) : ViewModel() {

    private var _name = ""

    private val _navigationChannel = Channel<NavDirections>()
    val navigationChannel: ReceiveChannel<NavDirections> = _navigationChannel

    private val _createCommandEnabled = MutableLiveData(false)
    val createCommandEnabled: LiveData<Boolean> = _createCommandEnabled

    fun onNameChanged(name: String) {
        if (_name != name) {
            _name = name
            _createCommandEnabled.value = _name.isNotBlank()
        }
    }

    fun onCreateCommand() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                createSavingsGoalInteractor.execute(
                    _name,
                    sessionState.accountId,
                    sessionState.accountCurreny
                )
                _navigationChannel.send(CreateSavingsGoalFragmentDirections.actionToSavingsGoalsDestination())
            } catch (e: ServiceException) {
                e.localizedMessage?.also {
                    _navigationChannel.send(NavGraphXmlDirections.toErrorDialog(it))
                }
            }
        }
    }
}

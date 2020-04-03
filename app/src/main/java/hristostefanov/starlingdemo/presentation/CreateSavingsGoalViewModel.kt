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
import java.math.BigDecimal
import java.util.*
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

    private val _liveName: MutableLiveData<String> = _savedStateHandle.getLiveData(NAME_KEY, "")

    private val _accountId: String = _savedStateHandle[ACCOUNT_ID_KEY]
        ?: throw IllegalArgumentException(ACCOUNT_ID_KEY)
    private val _accountCurrency: Currency = _savedStateHandle[ACCOUNT_CURRENCY_KEY]
        ?: throw IllegalArgumentException(ACCOUNT_CURRENCY_KEY)
    private val _roundUpAmount: BigDecimal = _savedStateHandle[ROUND_UP_AMOUNT_KEY]
        ?: throw IllegalArgumentException(ROUND_UP_AMOUNT_KEY)

    private val _navigationChannel = Channel<NavDirections>()
    val navigationChannel: ReceiveChannel<NavDirections> = _navigationChannel

    private val _createCommandEnabled =
        Transformations.map(_liveName) {
            it.isNotBlank()
        }
    val createCommandEnabled: LiveData<Boolean> = _createCommandEnabled

    fun onNameChanged(name: String) {
        _liveName.value = name
    }

    fun onCreateCommand() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                createSavingsGoalInteractor.execute(
                    _liveName.value!!,
                    _accountId,
                    _accountCurrency
                )
                // TODO consider navigating UP instead
                _navigationChannel.send(
                    CreateSavingsGoalFragmentDirections.actionToSavingsGoalsDestination(
                        _accountId,
                        _accountCurrency,
                        _roundUpAmount
                    )
                )
            } catch (e: ServiceException) {
                e.localizedMessage?.also {
                    _navigationChannel.send(NavGraphXmlDirections.toErrorDialog(it))
                }
            }
        }
    }
}

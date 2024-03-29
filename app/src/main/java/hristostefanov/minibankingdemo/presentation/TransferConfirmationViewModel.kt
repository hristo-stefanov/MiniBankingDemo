package hristostefanov.minibankingdemo.presentation

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.NavGraphXmlDirections
import hristostefanov.minibankingdemo.R
import hristostefanov.minibankingdemo.business.dependences.ServiceException
import hristostefanov.minibankingdemo.presentation.dependences.AmountFormatter
import hristostefanov.minibankingdemo.ui.TransferConfirmationFragmentArgs
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import hristostefanov.minibankingdemo.util.NavigationChannel
import hristostefanov.minibankingdemo.util.StringSupplier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val NAVIGATION_DELAY_MS = 2000L

@HiltViewModel
class TransferConfirmationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loginSessionRegistry: LoginSessionRegistry,
    private val stringSupplier: StringSupplier,
    private val amountFormatter: AmountFormatter,
    @NavigationChannel
    private val navigationChannel: Channel<Navigation>
) : ViewModel() {

    private val args = TransferConfirmationFragmentArgs.fromSavedStateHandle(savedStateHandle)

    private val _acknowledgement = Channel<String>()
    val acknowledgement = _acknowledgement.receiveAsFlow()

    private val _info = MutableLiveData("")
    val info: LiveData<String> = _info

    init {
        val amountFormatted = amountFormatter.format(
            args.roundUpAmount,
            args.accountCurrency.currencyCode
        )
        _info.value = stringSupplier.get(R.string.transferInfo)
            .format(amountFormatted, args.savingsGoal.name)
    }

    fun onConfirmCommand() {
        viewModelScope.launch {
            try {
                loginSessionRegistry?.component?.addMoneyIntoGoalInteractor?.execute(
                    args.accountId,
                    args.savingsGoal.id,
                    args.accountCurrency,
                    args.roundUpAmount
                )

                _acknowledgement.send(stringSupplier.get(R.string.success))
                delay(NAVIGATION_DELAY_MS)
                navigationChannel.send(Navigation.Before(R.id.savingsGoalsDestination))
            } catch (e: ServiceException) {
                e.localizedMessage?.also {
                    navigationChannel.send(Navigation.Forward(NavGraphXmlDirections.toErrorDialog(it)))
                }
            }
        }
    }
}
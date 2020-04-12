package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hristostefanov.starlingdemo.NavGraphXmlDirections
import hristostefanov.starlingdemo.R
import hristostefanov.starlingdemo.business.dependences.ServiceException
import hristostefanov.starlingdemo.business.interactors.AddMoneyIntoGoalInteractor
import hristostefanov.starlingdemo.presentation.dependences.AmountFormatter
import hristostefanov.starlingdemo.ui.TransferConfirmationFragmentArgs
import hristostefanov.starlingdemo.util.StringSupplier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.util.*
import javax.inject.Inject

private const val NAVIGATION_DELAY_MS = 2000L

class TransferConfirmationViewModel constructor(
    private val _args: TransferConfirmationFragmentArgs
) : ViewModel() {
    @Inject
    internal lateinit var _interactor: AddMoneyIntoGoalInteractor
    @Inject
    internal lateinit var _locale: Locale
    @Inject
    internal lateinit var _stringSupplier: StringSupplier
    @Inject
    internal lateinit var _amountFormatter: AmountFormatter
    @Inject
    internal lateinit var eventBus: EventBus

    private val _acknowledgementChannel = Channel<String>()
    val acknowledgementChannel: ReceiveChannel<String> = _acknowledgementChannel

    private val _info = MutableLiveData("")
    val info: LiveData<String> = _info

    @Inject
    internal fun init() {
        val amountFormatted = _amountFormatter.format(
            _args.roundUpAmount,
            _args.accountCurrency.currencyCode,
            _locale
        )
        _info.value = _stringSupplier.get(R.string.transferInfo)
             .format(amountFormatted, _args.savingsGoal.name)
    }

    fun onConfirmCommand() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _interactor.execute(
                    _args.accountId,
                    _args.savingsGoal.id,
                    _args.accountCurrency,
                    _args.roundUpAmount
                )

                _acknowledgementChannel.send(_stringSupplier.get(R.string.success))
                delay(NAVIGATION_DELAY_MS)
                // TODO consider thread-safety
                eventBus.post(Navigation.Before(R.id.savingsGoalsDestination))
            } catch (e: ServiceException) {
                e.localizedMessage?.also {
                    eventBus.post(Navigation.Forward(NavGraphXmlDirections.toErrorDialog(it)))
                }
            }
        }
    }
}
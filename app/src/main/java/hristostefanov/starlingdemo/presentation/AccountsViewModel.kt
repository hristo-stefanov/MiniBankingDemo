package hristostefanov.starlingdemo.presentation

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import hristostefanov.starlingdemo.NavGraphXmlDirections
import hristostefanov.starlingdemo.R
import hristostefanov.starlingdemo.business.dependences.ServiceException
import hristostefanov.starlingdemo.business.entities.Account
import hristostefanov.starlingdemo.business.interactors.CalcRoundUpInteractor
import hristostefanov.starlingdemo.business.interactors.ListAccountsInteractor
import hristostefanov.starlingdemo.presentation.dependences.AmountFormatter
import hristostefanov.starlingdemo.ui.AccountsFragmentDirections
import hristostefanov.starlingdemo.util.StringSupplier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class AccountsViewModel @Inject constructor(
    private val _sharedState: SharedState,
    private val _calcRoundUpInteractor: CalcRoundUpInteractor,
    private val _listAccountsInteractor: ListAccountsInteractor,
    private val _localeProvider: Provider<Locale>,
    private val _stringSupplier: StringSupplier,
    private val _amountFormatter: AmountFormatter
) : ViewModel() {
    private var _roundUpSinceDate: LocalDate = LocalDate.now().minusWeeks(1)
    private var _accounts: List<Account> = emptyList()

    private val _navigationChannel = Channel<NavDirections>()
    val navigationChannel: ReceiveChannel<NavDirections> = _navigationChannel

    private val _accountList = MutableLiveData<List<DisplayAccount>>(emptyList())
    val accountList: LiveData<List<DisplayAccount>> = _accountList

    private val _selectedAccountPosition = MutableLiveData(0)
    val selectedAccountPosition: LiveData<Int> = _selectedAccountPosition

    private val _roundUpAmount = MutableLiveData("")
    val roundUpAmount: LiveData<String> = _roundUpAmount

    private val _roundUpInfo = MutableLiveData("")
    val roundUpInfo: LiveData<String> = _roundUpInfo

    private val _transferCommandEnabled = MutableLiveData(false)
    val transferCommandEnabled: LiveData<Boolean> = _transferCommandEnabled

    fun onTransferCommand() {
        viewModelScope.launch {
            _navigationChannel.send(AccountsFragmentDirections.actionToSavingsGoalsDestination())
        }
    }

    fun onAccountSelectionChanged(position: Int) {
        if (position != _selectedAccountPosition.value) {
            _selectedAccountPosition.value = position
            viewModelScope.launch {
                updateStateWithSelectedAccount()
            }
        }
    }

    init {
        val formatter =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(_localeProvider.get())
        val sinceDateFormatted = _roundUpSinceDate.format(formatter)

        _roundUpInfo.value = _stringSupplier.get(R.string.roundUpInfo).format(sinceDateFormatted)

        viewModelScope.launch {
            _accounts = withContext(Dispatchers.IO) {
                try {
                    _listAccountsInteractor.execute()
                } catch (e: ServiceException) {
                    e.message?.also {
                        _navigationChannel.send(NavGraphXmlDirections.toErrorDialog(it))
                    }
                    emptyList<Account>()
                }
            }

            _accountList.value = _accounts.map {
                val displayBalance = _amountFormatter.format(
                    it.balance,
                    it.currency.currencyCode,
                    _localeProvider.get()
                )
                DisplayAccount(it.accountNum, it.currency.currencyCode, displayBalance)
            }

            updateStateWithSelectedAccount()
        }
    }

    @MainThread
    private suspend fun updateStateWithSelectedAccount() {
        val selectedAccount = _accounts.getOrNull(_selectedAccountPosition.value!!)

        if (selectedAccount != null) {
            _sharedState.accountId = selectedAccount.id
            _sharedState.accountCurreny = selectedAccount.currency
            _sharedState.roundUpAmount = withContext(Dispatchers.IO) {
                try {
                    _calcRoundUpInteractor.execute(
                        selectedAccount.id,
                        _roundUpSinceDate
                    )
                } catch (e: ServiceException) {
                    e.message?.also {
                        _navigationChannel.send(NavGraphXmlDirections.toErrorDialog(it))
                    }
                    BigDecimal.ZERO
                }
            }
        }

        _roundUpAmount.value = if (selectedAccount == null) {
            _stringSupplier.get(R.string.no_account)
        } else {
            _amountFormatter.format(
                _sharedState.roundUpAmount,
                _sharedState.accountCurreny.currencyCode,
                _localeProvider.get()
            )
        }
        _transferCommandEnabled.value = selectedAccount != null && _sharedState.roundUpAmount.signum() == 1 // is positive
    }
}
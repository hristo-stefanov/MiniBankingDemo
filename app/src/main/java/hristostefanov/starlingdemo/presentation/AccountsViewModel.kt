package hristostefanov.starlingdemo.presentation

import androidx.annotation.MainThread
import androidx.lifecycle.*
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

/**
 * Expected arguments passed through [SavedStateHandle]: None
 */
class AccountsViewModel constructor(
    private val _state: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val ACCOUNT_ID_KEY = "accountId"

        var SavedStateHandle.accountId: String?
            get() = this[ACCOUNT_ID_KEY]
            set(value) {
                this[ACCOUNT_ID_KEY] = value
            }
    }

    @Inject
    internal lateinit var _calcRoundUpInteractor: CalcRoundUpInteractor

    @Inject
    internal lateinit var _listAccountsInteractor: ListAccountsInteractor

    @Inject
    internal lateinit var _localeProvider: Provider<Locale>

    @Inject
    internal lateinit var _stringSupplier: StringSupplier

    @Inject
    internal lateinit var _amountFormatter: AmountFormatter

    private val _roundUpSinceDate: LocalDate = LocalDate.now().minusWeeks(1)
    private var _accounts: List<Account> = emptyList()
    private var _selectedAccount: Account? = null
    private var _roundUpAmount: BigDecimal? = null

    private val _navigationChannel = Channel<NavDirections>()
    val navigationChannel: ReceiveChannel<NavDirections> = _navigationChannel

    private val _accountList = MutableLiveData<List<DisplayAccount>>(emptyList())
    val accountList: LiveData<List<DisplayAccount>> = _accountList

    private val _selectedAccountPosition = MutableLiveData(0)
    val selectedAccountPosition: LiveData<Int> = _selectedAccountPosition

    private val _roundUpAmountText = MutableLiveData("")
    val roundUpAmountText: LiveData<String> = _roundUpAmountText

    private val _roundUpInfo = MutableLiveData("")
    val roundUpInfo: LiveData<String> = _roundUpInfo

    private val _transferCommandEnabled = MutableLiveData(false)
    val transferCommandEnabled: LiveData<Boolean> = _transferCommandEnabled

    fun onTransferCommand() {
        _selectedAccount?.also { account ->
            _roundUpAmount?.also { roundUpAmount ->
                viewModelScope.launch {
                    _navigationChannel.send(
                        AccountsFragmentDirections.actionToSavingsGoalsDestination(
                            account.id,
                            account.currency,
                            roundUpAmount
                        )
                    )
                }
            }
        }
    }

    fun onAccountSelectionChanged(position: Int) {
        val newAccountId = _accounts.getOrNull(position)?.id
        if (newAccountId != _state.accountId) {
            _state.accountId = newAccountId
            viewModelScope.launch {
                updateStateWithSelectedAccount()
            }
        }
    }

    @Inject
    fun init() {
        val formatter =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                .withLocale(_localeProvider.get())
        val sinceDateFormatted = _roundUpSinceDate.format(formatter)

        _roundUpInfo.value =
            _stringSupplier.get(R.string.roundUpInfo).format(sinceDateFormatted)

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

            // map Account to DisplayAccount
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
        _selectedAccount = _accounts.find { it.id == _state.accountId } ?: _accounts.getOrNull(0)

        _selectedAccountPosition.value = _accounts.indexOf(_selectedAccount)

        _roundUpAmount = _selectedAccount?.let { account ->
            withContext(Dispatchers.IO) {
                try {
                    _calcRoundUpInteractor.execute(
                        account.id,
                        _roundUpSinceDate
                    )
                } catch (e: ServiceException) {
                    e.message?.also {
                        _navigationChannel.send(NavGraphXmlDirections.toErrorDialog(it))
                    }
                    null
                }
            }
        }

        _roundUpAmountText.value = _roundUpAmount?.let { roundUpAmount ->
            _selectedAccount?.let { selectedAccount ->
                _amountFormatter.format(
                    roundUpAmount,
                    selectedAccount.currency.currencyCode,
                    _localeProvider.get()
                )
            }
        } ?: _stringSupplier.get(R.string.no_account)
    }
}
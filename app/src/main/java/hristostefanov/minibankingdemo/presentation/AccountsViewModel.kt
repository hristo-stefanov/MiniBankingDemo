package hristostefanov.minibankingdemo.presentation

import androidx.annotation.MainThread
import androidx.lifecycle.*
import hristostefanov.minibankingdemo.NavGraphXmlDirections
import hristostefanov.minibankingdemo.R
import hristostefanov.minibankingdemo.business.dependences.ServiceException
import hristostefanov.minibankingdemo.business.entities.Account
import hristostefanov.minibankingdemo.business.interactors.CalcRoundUpInteractor
import hristostefanov.minibankingdemo.business.interactors.DataSourceChangedEvent
import hristostefanov.minibankingdemo.business.interactors.ListAccountsInteractor
import hristostefanov.minibankingdemo.presentation.dependences.AmountFormatter
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.ui.AccountsFragmentDirections
import hristostefanov.minibankingdemo.util.NavigationChannel
import hristostefanov.minibankingdemo.util.SessionRegistry
import hristostefanov.minibankingdemo.util.StringSupplier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import javax.inject.Inject

class AccountsViewModel constructor(
    private val state: SavedStateHandle
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
    internal lateinit var calcRoundUpInteractor: CalcRoundUpInteractor

    @Inject
    internal lateinit var listAccountsInteractor: ListAccountsInteractor

    @Inject
    internal lateinit var locale: Locale

    @Inject
    internal lateinit var stringSupplier: StringSupplier

    @Inject
    internal lateinit var amountFormatter: AmountFormatter

    @Inject
    internal lateinit var eventBus: EventBus

    @Inject
    @NavigationChannel
    internal lateinit var navigationChannel: Channel<Navigation>

    @Inject
    internal lateinit var tokenStore: TokenStore

    @Inject
    internal lateinit var sessionRegistry: SessionRegistry

    private val roundUpSinceDate: LocalDate = LocalDate.now().minusWeeks(1)
    private var accounts: List<Account> = emptyList()
    private var selectedAccount: Account? = null
    private var roundUpAmount: BigDecimal? = null

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
        selectedAccount?.also { account ->
            roundUpAmount?.also { roundUpAmount ->
                viewModelScope.launch {
                    navigationChannel.send(
                        Navigation.Forward(
                            AccountsFragmentDirections.actionToSavingsGoalsDestination(
                                account.id,
                                account.currency,
                                roundUpAmount
                            )
                        )
                    )
                }
            }
        }
    }

    fun onAccountSelectionChanged(position: Int) {
        val newAccountId = accounts.getOrNull(position)?.id
        if (newAccountId != state.accountId) {
            state.accountId = newAccountId
            viewModelScope.launch {
                updateStateDependentOnSelectedAccount()
            }
        }
    }

    @Inject
    fun init() {
        load()
        eventBus.register(this)
    }

    public override fun onCleared() {
        eventBus.unregister(this)
        super.onCleared()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDataSourceChanged(event: DataSourceChangedEvent) {
        load()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAuthenticated(event: AuthenticatedEvent) {
        load()
    }

    private fun load() {
        if (tokenStore.token.isBlank()) {
            viewModelScope.launch {
                navigationChannel.send(Navigation.Forward(NavGraphXmlDirections.toAccessTokenDestination()))
            }
            return
        }

        val formatter =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                .withLocale(locale)
        val sinceDateFormatted = roundUpSinceDate.format(formatter)

        _roundUpInfo.value =
            stringSupplier.get(R.string.roundUpInfo).format(sinceDateFormatted)

        viewModelScope.launch {
            accounts = try {
                listAccountsInteractor.execute()
            } catch (e: ServiceException) {
                e.message?.also {
                    navigationChannel.send(
                        Navigation.Forward(
                            NavGraphXmlDirections.toErrorDialog(it)
                        )
                    )
                }
                emptyList<Account>()
            }

            // map Account to DisplayAccount
            _accountList.value = accounts.map {
                val displayBalance = amountFormatter.format(
                    it.balance,
                    it.currency.currencyCode
                )
                DisplayAccount(it.accountNum, it.currency.currencyCode, displayBalance)
            }

            updateStateDependentOnSelectedAccount()
        }
    }

    @MainThread
    private suspend fun updateStateDependentOnSelectedAccount() {
        selectedAccount = accounts.find { it.id == state.accountId } ?: accounts.getOrNull(0)

        _selectedAccountPosition.value = accounts.indexOf(selectedAccount)

        roundUpAmount = selectedAccount?.let { account ->
            try {
                calcRoundUpInteractor.execute(
                    account.id,
                    roundUpSinceDate
                )
            } catch (e: ServiceException) {
                e.message?.also {
                    navigationChannel.send(
                        Navigation.Forward(
                            NavGraphXmlDirections.toErrorDialog(it)
                        )
                    )
                }
                null
            }
        }

        _roundUpAmountText.value = roundUpAmount?.let { roundUpAmount ->
            selectedAccount?.let { selectedAccount ->
                amountFormatter.format(
                    roundUpAmount,
                    selectedAccount.currency.currencyCode
                )
            }
        } ?: stringSupplier.get(R.string.no_account)

        _transferCommandEnabled.value = roundUpAmount?.signum() == 1 // if positive
    }

    fun onLogout() {
        tokenStore.token = ""
        sessionRegistry.newSession()
        // restart to get deps from the new session
        viewModelScope.launch {
            navigationChannel.send(Navigation.Restart)
        }
    }
}
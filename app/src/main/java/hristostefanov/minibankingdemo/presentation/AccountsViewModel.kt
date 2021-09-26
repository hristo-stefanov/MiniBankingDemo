package hristostefanov.minibankingdemo.presentation

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
import kotlinx.coroutines.flow.*
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

const val ACCOUNT_ID_KEY = "accountId"

class AccountsViewModel constructor(
    private val state: SavedStateHandle
) : ViewModel() {

    private val savedAccountIdFlow: Flow<String?> = state.getLiveData<String>(ACCOUNT_ID_KEY, null).asFlow()

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
    private var accounts = MutableStateFlow<List<Account>>(emptyList())
    private var selectedAccount: Account? = null
    private var roundUpAmount: BigDecimal? = null

    private val _accountList = MutableStateFlow<List<DisplayAccount>>(emptyList())
    val accountList: StateFlow<List<DisplayAccount>> = _accountList

    private val _selectedAccountPosition = MutableStateFlow(-1)
    val selectedAccountPosition: StateFlow<Int> = _selectedAccountPosition

    private val _roundUpAmountText = MutableStateFlow("")
    val roundUpAmountText: StateFlow<String> = _roundUpAmountText

    private val _roundUpInfo = MutableStateFlow("")
    val roundUpInfo: StateFlow<String> = _roundUpInfo

    private val _transferCommandEnabled = MutableStateFlow(false)
    val transferCommandEnabled: StateFlow<Boolean> = _transferCommandEnabled

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
        val accountId = accounts.value.getOrNull(position)?.id
        state[ACCOUNT_ID_KEY] = accountId
    }

    @Inject
    fun init() {
        load()
        eventBus.register(this)

        // map Account to DisplayAccount
        accounts
            .map {
                it.map { account ->
                    val displayBalance = amountFormatter.format(
                        account.balance,
                        account.currency.currencyCode
                    )
                    DisplayAccount(account.accountNum, account.currency.currencyCode, displayBalance)
                }
            }
            .onEach {
                _accountList.value = it
            }
            .launchIn(viewModelScope)


        combine(savedAccountIdFlow, accounts) { accountId: String?, accounts: List<Account> ->
            val selectedAccount = accounts.find { it.id == accountId } ?: accounts.getOrNull(0)
            accounts.indexOf(selectedAccount)
        }
            .onEach {
                _selectedAccountPosition.value = it
            }
            .launchIn(viewModelScope)

        // TODO called twice
        val selectedAccountFlow = combine(_selectedAccountPosition, accounts) { position: Int, accounts: List<Account> ->
            accounts.getOrNull(position)
        }.distinctUntilChanged()

        val roundUpAmountFlow: Flow<BigDecimal?> = selectedAccountFlow
            .map { account ->
                account?.let {
                    calcRoundUpInteractor.execute(it.id, roundUpSinceDate)
                }
            }
            .catch { exception ->
                exception.message?.also {
                    navigationChannel.send(
                        Navigation.Forward(
                            NavGraphXmlDirections.toErrorDialog(it)
                        )
                    )
                }
                emit(null)
            }

        combine(
            selectedAccountFlow,
            roundUpAmountFlow
        ) { account: Account?, roundUpAmount: BigDecimal? ->
            if (account != null && roundUpAmount != null) {
                amountFormatter.format(
                    roundUpAmount,
                    account.currency.currencyCode
                )
            } else {
                stringSupplier.get(R.string.no_account)
            }
        }
            .onEach {
                _roundUpAmountText.value = it
            }
            .launchIn(viewModelScope)


        roundUpAmountFlow
            .map {
                it?.signum() == 1 // if positive
            }
            .onEach {
                _transferCommandEnabled.value = it
            }
            .launchIn(viewModelScope)
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
            accounts.value = try {
                listAccountsInteractor.execute()
            } catch (e: ServiceException) {
                e.message?.also {
                    navigationChannel.send(
                        Navigation.Forward(
                            NavGraphXmlDirections.toErrorDialog(it)
                        )
                    )
                }
                emptyList()
            }
        }
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
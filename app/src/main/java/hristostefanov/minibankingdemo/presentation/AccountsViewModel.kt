package hristostefanov.minibankingdemo.presentation

import android.R.attr.value
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.NavGraphXmlDirections
import hristostefanov.minibankingdemo.R
import hristostefanov.minibankingdemo.business.dependences.ServiceException
import hristostefanov.minibankingdemo.business.entities.Account
import hristostefanov.minibankingdemo.business.interactors.DataSourceChangedEvent
import hristostefanov.minibankingdemo.presentation.dependences.AmountFormatter
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.ui.AccountsFragmentDirections
import hristostefanov.minibankingdemo.usecase.input.Startup
import hristostefanov.minibankingdemo.usecase.output.AccountsAndRoundUpsSummary
import hristostefanov.minibankingdemo.util.NavigationChannel
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import hristostefanov.minibankingdemo.util.StringSupplier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.math.BigDecimal
import java.nio.file.Files.find
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine

const val ACCOUNT_ID_KEY = "accountId"

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val locale: Locale,
    private val stringSupplier: StringSupplier,
    private val amountFormatter: AmountFormatter,
    private val eventBus: EventBus,
    @NavigationChannel
    private val navigationChannel: Channel<Navigation>,
    private val tokenStore: TokenStore,
    private val loginSessionRegistry: LoginSessionRegistry,
    private val userInterface: UserInterfaceImpl
) : ViewModel() {

    private val savedAccountIdFlow: Flow<String?> =
        state.getStateFlow<String?>(ACCOUNT_ID_KEY, null)

    // TODO this business logic shouldn't be here
    private val roundUpSinceDate: LocalDate = LocalDate.now().minusWeeks(1)

    private val _accountList = MutableStateFlow<List<DisplayAccount>>(emptyList())
    val accountList: StateFlow<List<DisplayAccount>> = _accountList.asStateFlow()

    private val _selectedAccountPosition = MutableStateFlow(-1)
    val selectedAccountPosition: StateFlow<Int> = _selectedAccountPosition.asStateFlow()

    private val _roundUpAmountText = MutableStateFlow("")
    val roundUpAmountText: StateFlow<String> = _roundUpAmountText.asStateFlow()

    private val _roundUpInfo = MutableStateFlow("")
    val roundUpInfo: StateFlow<String> = _roundUpInfo.asStateFlow()

    private val _transferCommandEnabled = MutableStateFlow(false)
    val transferCommandEnabled: StateFlow<Boolean> = _transferCommandEnabled.asStateFlow()

    private val selectedAccountFlow: Flow<AccountsAndRoundUpsSummary.Item?> =
        combine(_selectedAccountPosition, userInterface.summary) { position: Int, summary: AccountsAndRoundUpsSummary? ->
            summary?.items?.getOrNull(position)
        }.distinctUntilChanged()

    fun onTransferCommand() {
        selectedAccountFlow
            .take(1)
            .filterNotNull()
            .map {
                Navigation.Forward(
                    AccountsFragmentDirections.actionToSavingsGoalsDestination(
                        it.accountId,
                        it.currency,
                        it.roundUp
                    )
                )
            }
            .onEach {
                navigationChannel.send(it)
            }
            .launchIn(viewModelScope)
    }

    fun onAccountSelectionChanged(position: Int) {
        val accountId = userInterface.summary.value?.items?.getOrNull(position)?.accountId
        state[ACCOUNT_ID_KEY] = accountId
    }

    init {
        load()
        eventBus.register(this)

        // map Account to DisplayAccount
        userInterface.summary.filterNotNull().map { it ->
            it.items.map { item ->
                val displayBalance = amountFormatter.format(
                    item.balance,
                    item.currency.currencyCode
                )
                DisplayAccount(
                    item.number,
                    item.currency.currencyCode,
                    displayBalance
                )
            }
        }
            .onEach { _accountList.value = it }
            .launchIn(viewModelScope)

        combine(savedAccountIdFlow, userInterface.summary.filterNotNull()) { accountId: String?, summary: AccountsAndRoundUpsSummary ->
            val selectedAccount = summary.items.find { it.accountId == accountId } ?: summary.items.getOrNull(0)
            summary.items.indexOf(selectedAccount)
        }
            .onEach {
                _selectedAccountPosition.value = it
            }
            .launchIn(viewModelScope)

        selectedAccountFlow
            .map {
                if (it != null) {
                    amountFormatter.format(
                        it.roundUp,
                        it.currency.currencyCode
                    )
                } else {
                    stringSupplier.get(R.string.no_account)
                }
            }
            .onEach {
                _roundUpAmountText.value = it
            }
            .launchIn(viewModelScope)

        selectedAccountFlow
            .map { it != null }
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
        val formatter =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                .withLocale(locale)
        val sinceDateFormatted = roundUpSinceDate.format(formatter)

        _roundUpInfo.value =
            stringSupplier.get(R.string.roundUpInfo).format(sinceDateFormatted)
    }

    fun onLogout() {
        tokenStore.token = ""
        loginSessionRegistry.close()
        // TODO this looks redundant since using SessionRegistry
        // restart to get deps from the new [SessionComponent]
        viewModelScope.launch {
            navigationChannel.send(Navigation.Restart)
        }
    }
}
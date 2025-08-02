package hristostefanov.minibankingdemo.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.R
import hristostefanov.minibankingdemo.presentation.dependences.AmountFormatter
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.ui.AccountsFragmentDirections
import hristostefanov.minibankingdemo.usecase.input.GetSummaryInteractor
import hristostefanov.minibankingdemo.usecase.output.Summary
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import hristostefanov.minibankingdemo.util.NavigationChannel
import hristostefanov.minibankingdemo.util.StringSupplier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import javax.inject.Inject

const val ACCOUNT_ID_KEY = "accountId"

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val locale: Locale,
    private val stringSupplier: StringSupplier,
    private val amountFormatter: AmountFormatter,
    @NavigationChannel
    private val navigationChannel: Channel<Navigation>,
    private val tokenStore: TokenStore,
    private val loginSessionRegistry: LoginSessionRegistry,
    private val userInterface: UserInterfaceImpl,
    private val getSummaryInteractor: GetSummaryInteractor
) : ViewModel() {

    private val savedAccountIdFlow: Flow<String?> =
        state.getStateFlow<String?>(ACCOUNT_ID_KEY, null)

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

    private val selectedAccountFlow: Flow<Summary.Item?> =
        combine(_selectedAccountPosition, userInterface.summary) { position: Int, summary: Summary? ->
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

        userInterface.summary.filterNotNull().map { it ->
            // TODO consider externalizing similarly to AmountFormatter
            val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
            it.roundUpSince.format(formatter)
        }
            .onEach {
                _roundUpInfo.value = stringSupplier.get(R.string.roundUpInfo).format(it)
            }
            .launchIn(viewModelScope)

        combine(savedAccountIdFlow, userInterface.summary.filterNotNull()) { accountId: String?, summary: Summary ->
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

    fun onLogout() {
        tokenStore.token = ""
        loginSessionRegistry.close()
        // TODO this looks redundant since using SessionRegistry
        // restart to get deps from the new [SessionComponent]
        viewModelScope.launch {
            navigationChannel.send(Navigation.Restart)
        }
    }

    fun onRefresh() {
        viewModelScope.launch {
            getSummaryInteractor.start(userInterface)
        }
    }
}
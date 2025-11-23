package hristostefanov.minibankingdemo.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.R
import hristostefanov.minibankingdemo.presentation.dependences.AmountFormatter
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.ui.AccountsFragmentDirections
import hristostefanov.minibankingdemo.usecase.input.LogoutInteractor
import hristostefanov.minibankingdemo.usecase.input.ViewSummaryInteractor
import hristostefanov.minibankingdemo.usecase.input.isCancellation
import hristostefanov.minibankingdemo.usecase.input.isFailure
import hristostefanov.minibankingdemo.usecase.output.Summary
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import hristostefanov.minibankingdemo.util.MainCommandChannel
import hristostefanov.minibankingdemo.util.StringSupplier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import javax.inject.Inject
import kotlin.collections.map
import kotlin.collections.toTypedArray

const val ACCOUNT_ID_KEY = "accountId"

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val locale: Locale,
    private val stringSupplier: StringSupplier,
    private val amountFormatter: AmountFormatter,
    @MainCommandChannel
    private val mainCommandChannel: Channel<MainCommand>,
    private val tokenStore: TokenStore,
    private val loginSessionRegistry: LoginSessionRegistry,
    private val mainUI: MainUI,
    private val viewSummaryInteractor: ViewSummaryInteractor,
    private val logoutInteractor: LogoutInteractor,
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

    private val _logoutCommandEnabled = MutableStateFlow(false)
    val logoutCommandEnabled: StateFlow<Boolean> = _logoutCommandEnabled.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val summary: Flow<Summary?> = loginSessionRegistry.componentFlow.flatMapLatest { component ->
       component?.data?.summary ?: flowOf(null)
    }

    // TODO this can go in session data, no? We already have Summary there
    private val selectedAccountFlow: Flow<Summary.Item?> =
        combine(_selectedAccountPosition, summary) { position: Int, summary: Summary? ->
            summary?.items?.getOrNull(position)
        }.distinctUntilChanged()

    private fun clearInMemoryLoginSessionData() {
        // TODO what to clear here
    }

    fun onTransferCommand() {
        selectedAccountFlow
            .take(1)
            .filterNotNull()
            .onEach { it ->
                val displaySavingsGoals = it.savingsGoals.map { DisplaySavingsGoal(it.id, it.name) }
                mainCommandChannel.send(
                    MainCommand.NavigateForward(
                        AccountsFragmentDirections.actionToSavingsGoalsDestination(
                            "Select destination",
                            displaySavingsGoals.toTypedArray()
                        )
                    )
                )
            }
            .launchIn(viewModelScope)
    }

    fun onAccountSelectionChanged(position: Int) {
        val accountId = loginSessionRegistry.requireComponent.data.summary.value?.items?.getOrNull(position)?.accountId
        // TODO do we really need to save it
        state[ACCOUNT_ID_KEY] = accountId
    }

    init {
        // map Account to DisplayAccount
        summary.map { it ->
            it?.items?.map { item ->
                val displayBalance = amountFormatter.format(
                    item.balance,
                    item.currency.currencyCode
                )
                DisplayAccount(
                    item.number,
                    item.currency.currencyCode,
                    displayBalance
                )
            } ?: emptyList()
        }
            .onEach { _accountList.value = it }
            .launchIn(viewModelScope)

        summary.map { it ->
            // TODO consider externalizing similarly to AmountFormatter
            val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
            it?.roundUpSince?.format(formatter)
        }
            .onEach { date ->
                val text = date?.let { stringSupplier.get(R.string.roundUpInfo, it) } ?: ""
                _roundUpInfo.value = text
            }
            .launchIn(viewModelScope)

        combine(savedAccountIdFlow, summary.filterNotNull()) { accountId: String?, summary: Summary ->
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

        selectedAccountFlow.filterNotNull()
            .onEach {
                loginSessionRegistry.requireComponent.data.selectedAccount = it
            }
            .launchIn(viewModelScope)

        selectedAccountFlow
            .map { it != null }
            .onEach {
                _transferCommandEnabled.value = it
            }
            .launchIn(viewModelScope)

        tokenStore.tokenFlow
            .map { it != null }
            .onEach {
                _logoutCommandEnabled.value = it
            }
            .launchIn(viewModelScope)

        // TODO consider making SessionRegistry observable instead
        tokenStore.tokenFlow.onEach {
            if (it == null) clearInMemoryLoginSessionData()
        }.launchIn(viewModelScope)

        viewSummary()
    }

    fun onLogout() {
        viewModelScope.launch {
            logoutInteractor()
        }
    }

    fun onRefresh() {
        viewSummary()
    }

    private fun viewSummary() {
        viewModelScope.launch {
            val status = viewSummaryInteractor()
            if (status.isFailure()) {
                mainUI.presentStatus(status)
            } else if(status.isCancellation()) {
                mainUI.presentMessage("Use the Refresh command to retry")
            }
        }
    }
}
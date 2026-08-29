package hristostefanov.minibankingdemo.presentation

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

@HiltViewModel
class AccountsViewModel @Inject constructor(
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private val selectedAccountIdFlow = loginSessionRegistry.componentFlow.flatMapLatest { component ->
        component?.data?.selectedAccountIdFlow ?: flowOf(null)
    }

    private fun getSelectedAccountFlow() = combine(_selectedAccountPosition, summary) { position: Int, summary: Summary? ->
        summary?.items?.getOrNull(position)
    }.distinctUntilChanged()

    fun onTransferCommand() {
        loginSessionRegistry.component?.data?.summary?.value?.items?.getOrNull(_selectedAccountPosition.value)?.let { account ->
            val displaySavingsGoals = account.savingsGoals.map { DisplaySavingsGoal(it.id, it.name) }
            viewModelScope.launch {
                mainCommandChannel.send(
                    MainCommand.NavigateForward(
                        AccountsFragmentDirections.actionToSavingsGoalsDestination(
                            "Select destination",
                            displaySavingsGoals.toTypedArray()
                        )
                    )
                )
            }
        }
    }

    fun onAccountSelectionChanged(position: Int) {
        loginSessionRegistry.component?.data?.let { data ->
            val accountId = data.summary.value?.items?.getOrNull(position)?.accountId
            data.selectedAccountIdFlow.value = accountId
        }
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

        combine(selectedAccountIdFlow, summary.filterNotNull()) { accountId: String?, summary: Summary ->
            val selectedAccount = summary.items.find { it.accountId == accountId } ?: summary.items.getOrNull(0)
            summary.items.indexOf(selectedAccount)
        }
            .onEach {
                _selectedAccountPosition.value = it
            }
            .launchIn(viewModelScope)

        getSelectedAccountFlow()
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

        getSelectedAccountFlow()
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
package hristostefanov.minibankingdemo.integrationtest

import androidx.lifecycle.SavedStateHandle
import hristostefanov.minibankingdemo.business.interactors.CalcRoundUpInteractor
import hristostefanov.minibankingdemo.business.interactors.ListAccountsInteractor
import hristostefanov.minibankingdemo.data.models.AccountV2
import hristostefanov.minibankingdemo.data.models.CurrencyAndAmount
import hristostefanov.minibankingdemo.data.models.FeedItem
import hristostefanov.minibankingdemo.presentation.AccountsViewModel
import hristostefanov.minibankingdemo.presentation.Navigation
import hristostefanov.minibankingdemo.presentation.dependences.AmountFormatter
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.util.NavigationChannel
import hristostefanov.minibankingdemo.util.StringSupplier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

class ServiceStubTestAutomation @Inject constructor(
    private val serviceStub: ServiceStub,
    private val calcRoundUpInteractor: CalcRoundUpInteractor,
    private val listAccountsInteractor: ListAccountsInteractor,
    private val stringSupplier: StringSupplier,
    private val amountFormatter: AmountFormatter,
    private val eventBus: EventBus,
    @NavigationChannel
    private val navigationChannel: Channel<Navigation>,
    private val tokenStore: TokenStore
) : TestAutomation {


    override fun calculateRoundUp(accountNumber: String): BigDecimal {
        return runBlocking { calcRoundUpInteractor.execute(accountNumber, LocalDate.now()) }
    }

    override fun theCalculatedRoundUpIsOne() {
        createAccount("1", "GBP", listOf(-0.4.toBigDecimal(), -0.6.toBigDecimal()))
    }

    override fun openAccountScreen(): AccountsViewModel {
        val state = SavedStateHandle()
        return AccountsViewModel(state).also {
            // manual field and method injection
            it.calcRoundUpInteractor = calcRoundUpInteractor
            it.listAccountsInteractor = listAccountsInteractor
            it.locale = Locale.UK
            it.stringSupplier = stringSupplier
            it.amountFormatter = amountFormatter
            it.eventBus = eventBus
            it.navigationChannel = navigationChannel
            it.tokenStore = tokenStore
            it.init()
        }
    }

    override fun login() {
        tokenStore.token = "token"
    }

    override fun createAccount(number: String, currency: String, transactions: List<BigDecimal>) {
        val feedItems = mutableListOf<FeedItem>()
        transactions.forEach { amount ->
            val feedItem = FeedItem(
                direction = if (amount.signum() >= 0) "IN" else "OUT",
                amount = CurrencyAndAmount(currency, amount.abs().unscaledValue().toLong()),
                status = "SETTLED"
            )
            feedItems.add(feedItem)
        }
        serviceStub.accounts = serviceStub.accounts + listOf(
            AccountV2(
                accountUid = number, defaultCategory = "", currency = currency
            )
        )
        serviceStub.feedItemsToAccountId = mapOf(number to feedItems)
    }
}
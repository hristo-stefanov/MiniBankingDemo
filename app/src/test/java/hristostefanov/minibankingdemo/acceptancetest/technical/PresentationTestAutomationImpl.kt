package hristostefanov.minibankingdemo.acceptancetest.technical

import androidx.lifecycle.SavedStateHandle
import hristostefanov.minibankingdemo.acceptancetest.businessflow.PresentationTestAutomation
import hristostefanov.minibankingdemo.business.entities.Account
import hristostefanov.minibankingdemo.business.interactors.ICalcRoundUpInteractor
import hristostefanov.minibankingdemo.business.interactors.IListAccountsInteractor
import hristostefanov.minibankingdemo.business.interactors.ListAccountsInteractor
import hristostefanov.minibankingdemo.presentation.AccountsViewModel
import hristostefanov.minibankingdemo.presentation.Navigation
import hristostefanov.minibankingdemo.presentation.dependences.AmountFormatter
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.util.NavigationChannel
import hristostefanov.minibankingdemo.util.StringSupplier
import kotlinx.coroutines.channels.Channel
import org.greenrobot.eventbus.EventBus
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

class PresentationTestAutomationImpl @Inject constructor(
    private val stringSupplier: StringSupplier,
    private val amountFormatter: AmountFormatter,
    private val eventBus: EventBus,
    @NavigationChannel
    private val navigationChannel: Channel<Navigation>,
    private val tokenStore: TokenStore
) : PresentationTestAutomation {

    private lateinit var listAccountsInteractor: IListAccountsInteractor
    private lateinit var calcRoundUpInteractor: ICalcRoundUpInteractor

    override fun login() {
        tokenStore.token = "token"
    }

    override fun calculatedRoundUpIs(amount: BigDecimal) {
        listAccountsInteractor = object : IListAccountsInteractor {
            override suspend fun execute(): List<Account> {
                return listOf(Account(
                    "1",
                    "12345678",
                    "",
                    Currency.getInstance("GBP"),
                    "100.00".toBigDecimal()
                ))
            }
        }

        calcRoundUpInteractor = object : ICalcRoundUpInteractor {
            override suspend fun execute(accountId: String, sinceDate: LocalDate): BigDecimal {
                return amount
            }
        }
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
}
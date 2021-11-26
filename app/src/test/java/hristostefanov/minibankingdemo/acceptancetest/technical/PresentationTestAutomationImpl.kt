package hristostefanov.minibankingdemo.acceptancetest.technical

import androidx.lifecycle.SavedStateHandle
import hristostefanov.minibankingdemo.acceptancetest.businessflow.PresentationTestAutomation
import hristostefanov.minibankingdemo.business.entities.Account
import hristostefanov.minibankingdemo.business.interactors.*
import hristostefanov.minibankingdemo.presentation.AccessTokenViewModel
import hristostefanov.minibankingdemo.presentation.AccountsViewModel
import hristostefanov.minibankingdemo.presentation.Navigation
import hristostefanov.minibankingdemo.presentation.dependences.AmountFormatter
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.util.*
import kotlinx.coroutines.channels.Channel
import org.greenrobot.eventbus.EventBus
import java.lang.AssertionError
import java.lang.IllegalStateException
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

val CORRECT_TOKEN = "correctToken"

class PresentationTestAutomationImpl @Inject constructor(
    private val stringSupplier: StringSupplier,
    private val amountFormatter: AmountFormatter,
    private val eventBus: EventBus,
    @NavigationChannel
    private val navigationChannel: Channel<Navigation>,
    private val tokenStore: TokenStore,
) : PresentationTestAutomation {

    private lateinit var listAccountsInteractor: ListAccountsInteractor
    private lateinit var calcRoundUpInteractor: CalcRoundUpInteractor

    private val sessionComponentFactory: SessionComponent.Factory = object: SessionComponent.Factory {
        override fun create(token: String): SessionComponent {
            return object : SessionComponent {
                override val calcRoundUpInteractor: CalcRoundUpInteractor
                    get() = this@PresentationTestAutomationImpl.calcRoundUpInteractor
                override val listAccountsInteractor: ListAccountsInteractor
                    get() = this@PresentationTestAutomationImpl.listAccountsInteractor
                override val listSavingGoalInteractor: ListSavingGoalsInteractor
                    get() = throw AssertionError()
                override val addMoneyIntoGoalInteractor: AddMoneyIntoGoalInteractor
                    get() = throw AssertionError()
                override val createSavingGoalsInteractor: CreateSavingsGoalInteractor
                    get() = throw AssertionError()
                override val token: String
                    get() = token
            }
        }
    }

    private val sessionRegistry = SessionRegistryImp(sessionComponentFactory)

    override fun login() {
        tokenStore.token = CORRECT_TOKEN
        sessionRegistry.createSession(tokenStore.token)
    }

    override fun calculatedRoundUpIs(amount: BigDecimal) {
        listAccountsInteractor = object : ListAccountsInteractor {
            override suspend fun execute(): List<Account> {
                if(sessionRegistry.sessionComponent?.token == CORRECT_TOKEN) {
                    return listOf(
                        Account(
                            "1",
                            "12345678",
                            "",
                            Currency.getInstance("GBP"),
                            "100.00".toBigDecimal()
                        )
                    )
                } else {
                    // TODO consider special exception for not authorized
                    throw IllegalStateException()
                }
            }
        }

        calcRoundUpInteractor = object : CalcRoundUpInteractor {
            override suspend fun execute(accountId: String, sinceDate: LocalDate): BigDecimal {
                return amount
            }
        }
    }

    override fun openAccountScreen(): AccountsViewModel {
        val state = SavedStateHandle()
        return AccountsViewModel(
            state,
            Locale.UK,
            stringSupplier,
            amountFormatter,
            eventBus,
            navigationChannel,
            tokenStore,
            sessionRegistry
        )
    }

    override fun openLoginScreen(): AccessTokenViewModel {
        return AccessTokenViewModel(
            tokenStore,
            sessionRegistry,
            navigationChannel,
            eventBus
        )
    }
}
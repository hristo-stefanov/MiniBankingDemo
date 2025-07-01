package hristostefanov.minibankingdemo.acceptancetest.technical

import androidx.lifecycle.SavedStateHandle
import hristostefanov.minibankingdemo.acceptancetest.businessflow.PresentationTestAutomation
import hristostefanov.minibankingdemo.business.dependences.ServiceException
import hristostefanov.minibankingdemo.business.entities.Account
import hristostefanov.minibankingdemo.business.interactors.*
import hristostefanov.minibankingdemo.presentation.LoginViewModel
import hristostefanov.minibankingdemo.presentation.AccountsViewModel
import hristostefanov.minibankingdemo.presentation.Navigation
import hristostefanov.minibankingdemo.presentation.UserInterfaceImpl
import hristostefanov.minibankingdemo.presentation.dependences.AmountFormatter
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.usecase.StartupInteractor
import hristostefanov.minibankingdemo.usecase.input.PresentAccountsAndRoundupsSummary
import hristostefanov.minibankingdemo.util.*
import kotlinx.coroutines.channels.Channel
import org.greenrobot.eventbus.EventBus
import org.robolectric.shadows.ShadowSystemProperties.override
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
    private val tokenStore: TokenStore,
) : PresentationTestAutomation {

    private var listAccountsInteractorStub: ListAccountsInteractor = object : ListAccountsInteractor {
        // dummy implementation
        override suspend fun execute(): List<Account> {
            return emptyList()
        }
    }
    private var calcRoundUpInteractorStub: CalcRoundUpInteractor = object : CalcRoundUpInteractor {
        // dummy implementation
        override suspend fun execute(accountId: String, sinceDate: LocalDate): BigDecimal {
            return "0.00".toBigDecimal()
        }
    }

    private lateinit var correctAccessToken: String

    private var isThereInternetConnection = true

    override fun thereIsNoInternetConnection() {
        isThereInternetConnection = false
    }

    private val loginSessionComponentFactory: LoginSessionComponent.Factory = object: LoginSessionComponent.Factory {
        override fun create(token: String, tokenType: String): LoginSessionComponent {
            return object : LoginSessionComponent {
                override val calcRoundUpInteractor: CalcRoundUpInteractor
                    get() = this@PresentationTestAutomationImpl.calcRoundUpInteractorStub
                override val listAccountsInteractor: ListAccountsInteractor
                    get() = this@PresentationTestAutomationImpl.listAccountsInteractorStub
                override val listSavingGoalInteractor: ListSavingGoalsInteractor
                    get() = throw AssertionError()
                override val addMoneyIntoGoalInteractor: AddMoneyIntoGoalInteractor
                    get() = throw AssertionError()
                override val createSavingGoalsInteractor: CreateSavingsGoalInteractor
                    get() = throw AssertionError()
                override val accessToken: String
                    get() = token
                override val presentAccountsAndRoundupsSummary: PresentAccountsAndRoundupsSummary
                    get() = TODO("Not yet implemented")
            }
        }
    }

    private val sessionRegistry = LoginSessionRegistryImp(loginSessionComponentFactory)

    private val startupInteractor = StartupInteractor(sessionRegistry, tokenStore, eventBus)

    private val userInterface = UserInterfaceImpl(navigationChannel)

    override suspend fun startUp() {
        startupInteractor.launchApp(userInterface)
    }
    override fun correctAccessTokenIs(accessToken: String) {
        correctAccessToken = accessToken
    }

    override fun savedAccessTokenIs(accessToken: String) {
        tokenStore.token = accessToken
    }

    override fun accountIn(currencyCode: String) {
        listAccountsInteractorStub = object : ListAccountsInteractor {
            override suspend fun execute(): List<Account> {
                // simulate auth check in the data layer
                if(sessionRegistry.component?.accessToken == correctAccessToken) {
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
                    throw ServiceException("401: Unauthorized")
                }
            }
        }
    }

    override fun calculatedRoundUpIs(amount: BigDecimal) {
        calcRoundUpInteractorStub = object : CalcRoundUpInteractor {
            override suspend fun execute(accountId: String, sinceDate: LocalDate): BigDecimal {
                // simulate auth check in the data layer
                if(sessionRegistry.component?.accessToken == correctAccessToken) {
                    return amount
                } else {
                    throw ServiceException("401: Unauthorized")
                }
            }
        }
    }

    override fun openAccountScreen(): AccountsViewModel {
        val state = SavedStateHandle()
        // TODO
        return AccountsViewModel(
            state,
            Locale.UK,
            stringSupplier,
            amountFormatter,
            navigationChannel,
            tokenStore,
            sessionRegistry,
            userInterface
        )
    }

    override fun openLoginScreen(): LoginViewModel {
        return LoginViewModel(
            navigationChannel,
            userInterface
        )
    }
}
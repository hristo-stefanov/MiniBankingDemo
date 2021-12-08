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
import hristostefanov.minibankingdemo.util.oauth.AccessTokenResponse
import hristostefanov.minibankingdemo.util.oauth.OAuth
import io.cucumber.messages.internal.com.google.protobuf.ServiceException
import kotlinx.coroutines.channels.Channel
import okhttp3.ResponseBody.Companion.toResponseBody
import org.greenrobot.eventbus.EventBus
import retrofit2.HttpException
import retrofit2.Response
import java.lang.AssertionError
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

private const val CORRECT_ACCESS_TOKEN = "correctAccessToken"

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

    private val oAuthStub = object: OAuth {
        override suspend fun accessToken(
            client_id: String,
            client_secret: String,
            grant_type: String,
            refreshToken: String
        ): AccessTokenResponse {
            if (refreshToken == correctRefreshToken) {
                return AccessTokenResponse(
                    access_token = CORRECT_ACCESS_TOKEN,
                    refresh_token = "newRefreshToken",
                    token_type = "Bearer",
                    expires_in = 0,
                    scope = ""
                )
            } else {
                throw HttpException(Response.error<String>(401, "".toResponseBody()))
            }
        }
    }

    private lateinit var correctRefreshToken: String

    private val sessionComponentFactory: SessionComponent.Factory = object: SessionComponent.Factory {
        override fun create(token: String, tokenType: String): SessionComponent {
            return object : SessionComponent {
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
            }
        }
    }

    private val sessionRegistry = SessionRegistryImp(sessionComponentFactory)

    override fun correctRefreshTokenIs(refreshToken: String) {
        correctRefreshToken = refreshToken
    }

    override fun savedRefreshTokenIs(refreshToken: String) {
        tokenStore.refreshToken = refreshToken
    }

    override fun accountIn(currencyCode: String) {
        listAccountsInteractorStub = object : ListAccountsInteractor {
            override suspend fun execute(): List<Account> {
                // simulate auth check in the data layer
                if(sessionRegistry.sessionComponent?.accessToken == CORRECT_ACCESS_TOKEN) {
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
                if(sessionRegistry.sessionComponent?.accessToken == CORRECT_ACCESS_TOKEN ) {
                    return amount
                } else {
                    throw ServiceException("401: Unauthorized")
                }
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
            eventBus,
            oAuthStub
        )
    }
}
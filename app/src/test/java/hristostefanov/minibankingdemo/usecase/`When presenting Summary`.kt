package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.any
import hristostefanov.minibankingdemo.business.calcStartOfSevenDayWindowIncludingToday
import hristostefanov.minibankingdemo.business.dependences.APIException
import hristostefanov.minibankingdemo.business.dependences.AuthException
import hristostefanov.minibankingdemo.business.dependences.NetworkException
import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.entities.Account
import hristostefanov.minibankingdemo.business.entities.Source
import hristostefanov.minibankingdemo.business.entities.Status
import hristostefanov.minibankingdemo.business.entities.Transaction
import hristostefanov.minibankingdemo.usecase.output.Summary
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Before
import java.time.OffsetDateTime
import org.mockito.BDDMockito.then
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import java.math.BigDecimal
import java.util.Currency
import javax.inject.Provider
import org.assertj.core.api.Assertions.assertThat
import kotlin.jvm.java

class `When presenting Summary` {
    private val repository: Repository = mock()
    private val userInterface: UserInterface = mock()
    private val nowProvider: Provider<OffsetDateTime> = mock()

    private val accounts = listOf(
        Account(
            id = "1",
            accountNum = "111",
            categoryUid = "cat",
            currency = Currency.getInstance("GBP"),
            balance = BigDecimal("100.20")
        ))

    private val spendingTransactions = listOf(
        Transaction(
            amount = BigDecimal("-2.56"),
            status = Status.SETTLED,
            source = Source.EXTERNAL,
            title = "tx",
            id = "a"
        )
    )

    private val expectedSummary = Summary(
       roundUpSince = OffsetDateTime.parse("2025-05-05T00:00+01"),
        items = listOf(
            Summary.Item(
                accountId = "1",
                number = "111",
                currency = Currency.getInstance("GBP"),
                roundUp = BigDecimal("0.44"),
                balance = BigDecimal("100.20")
            )
        )
    )

    private val interactor = GetSummaryInteractorImpl(
        repository = repository,
        nowProvider = nowProvider,
        calcSincePolicy = ::calcStartOfSevenDayWindowIncludingToday
    )

    @Before
    fun beforeEach() = runTest {
        given(nowProvider.get()).willReturn(OffsetDateTime.parse("2025-05-11T12:15+01"))
    }

    @Test
    fun `should present summary when everything succeeds`() = runTest {
        given(repository.findAllAccounts()).willReturn(accounts)
        given(repository.findTransactions(any(), any())).willReturn(spendingTransactions)

        interactor.start(userInterface)

        then(userInterface).should().presentSummary(expectedSummary)
    }

    @Test
    fun `should cancel flow and return error when auth fails`() = runTest {
        given(repository.findAllAccounts()).willThrow(AuthException())

        val result = interactor.start(userInterface)

        assertThat(result.exceptionOrNull()).isInstanceOf(AuthException::class.java)
    }

    @Test
    fun `should retry when API error occurs and user confirms retry`() = runTest {
        given(repository.findAllAccounts()).willThrow(APIException("500")).willReturn(accounts)
        given(repository.findTransactions(any(), any())).willReturn(spendingTransactions)

        given(userInterface.promptUserToRetryRecovery("500")).willReturn(true)

        interactor.invoke(userInterface)

        then(userInterface).should().promptUserToRetryRecovery("500")
        then(userInterface).should().presentSummary(expectedSummary)
    }

    @Test
    fun `should cancel flow and return error when user declines retry after API error`()  = runTest {
        given(repository.findAllAccounts()).willThrow(APIException("500")).willReturn(accounts)
        given(repository.findTransactions(any(), any())).willReturn(spendingTransactions)

        given(userInterface.promptUserToRetryRecovery("500")).willReturn(false)

        val result = interactor.invoke(userInterface)
        then(userInterface).should().promptUserToRetryRecovery("500")
        assertThat(result.exceptionOrNull()).isInstanceOfSatisfying(APIException::class.java) {
            assertThat(it.message).isEqualTo("500")
        }
    }

    fun `should retry when network error occurs and user confirms retry`() = runTest {
        given(repository.findAllAccounts()).willThrow(NetworkException("No route to host")).willReturn(accounts)
        given(repository.findTransactions(any(), any())).willReturn(spendingTransactions)

        given(userInterface.promptUserToRetryRecovery("No route to host")).willReturn(true)

        interactor.invoke(userInterface)

        then(userInterface).should().promptUserToRetryRecovery("No route to host")
        then(userInterface).should().presentSummary(expectedSummary)
    }

    @Test
    fun `should cancel flow and return error when user declines retry after network error`()  = runTest {
        given(repository.findAllAccounts()).willThrow(NetworkException("No route to host")).willReturn(accounts)
        given(repository.findTransactions(any(), any())).willReturn(spendingTransactions)

        given(userInterface.promptUserToRetryRecovery("No route to host")).willReturn(false)

        val result = interactor.invoke(userInterface)
        then(userInterface).should().promptUserToRetryRecovery("No route to host")
        assertThat(result.exceptionOrNull()).isInstanceOfSatisfying(NetworkException::class.java) {
            assertThat(it.message).isEqualTo("No route to host")
        }
    }
}
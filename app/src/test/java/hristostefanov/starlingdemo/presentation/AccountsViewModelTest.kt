package hristostefanov.starlingdemo.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import hristostefanov.starlingdemo.R
import hristostefanov.starlingdemo.any
import hristostefanov.starlingdemo.business.entities.Account
import hristostefanov.starlingdemo.business.interactors.CalcRoundUpInteractor
import hristostefanov.starlingdemo.business.interactors.ListAccountsInteractor
import hristostefanov.starlingdemo.presentation.dependences.AmountFormatter
import hristostefanov.starlingdemo.util.StringSupplier
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Test

import org.junit.Before
import org.junit.Rule
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock
import org.mockito.Mockito.timeout
import java.time.LocalDate
import java.util.*
import javax.inject.Provider

private const val TIMEOUT = 100L

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class AccountsViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    private val sharedState = SessionState()
    private val calcRoundUpInteractor = mock(CalcRoundUpInteractor::class.java)
    private val listAccountsInteractor = mock(ListAccountsInteractor::class.java)
    private val localeProvider: Provider<*> = mock(Provider::class.java)
    private val zoneIdProvider: Provider<*> = mock(Provider::class.java)
    private val stringSupplier = mock(StringSupplier::class.java)
    private val amountFormatter = mock(AmountFormatter::class.java)

    private val account1 = Account(
        "1",
        "111",
        "cat1",
        Currency.getInstance("GBP"),
        "100".toBigDecimal()
    )

    @Suppress("UNCHECKED_CAST")
    private val viewModel by lazy {
        AccountsViewModel(
            sharedState,
            calcRoundUpInteractor,
            listAccountsInteractor,
            localeProvider as Provider<Locale>,
            stringSupplier,
            amountFormatter
        )
    }


    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun testInit() = runBlocking {
        given(listAccountsInteractor.execute()).willReturn(listOf(account1))
        given(localeProvider.get()).willReturn(Locale.UK)
        given(stringSupplier.get(R.string.roundUpInfo)).willReturn("Round up amount since %s")
        given(amountFormatter.format(any(), any(), any())).willReturn("")

        viewModel // instantiate

        then(listAccountsInteractor).should(timeout(TIMEOUT)).execute()
        then(listAccountsInteractor).shouldHaveNoMoreInteractions()
        then(calcRoundUpInteractor).should(timeout(TIMEOUT))
            .execute(account1.id, LocalDate.now().minusWeeks(1))
        then(calcRoundUpInteractor).shouldHaveNoMoreInteractions()

        Unit
    }
}
package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.SavedStateHandle
import hristostefanov.starlingdemo.R
import hristostefanov.starlingdemo.any
import hristostefanov.starlingdemo.business.entities.Account
import hristostefanov.starlingdemo.business.interactors.CalcRoundUpInteractor
import hristostefanov.starlingdemo.business.interactors.ListAccountsInteractor
import hristostefanov.starlingdemo.presentation.AccountsViewModel.Companion.accountId
import hristostefanov.starlingdemo.presentation.dependences.AmountFormatter
import hristostefanov.starlingdemo.util.StringSupplier
import kotlinx.coroutines.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock
import org.mockito.Mockito.timeout
import java.time.LocalDate
import java.util.*
import javax.inject.Provider

private const val TIMEOUT = 100L

//@ExperimentalCoroutinesApi
//@ObsoleteCoroutinesApi
class AccountsViewModelTest: BaseViewModelTest() {
    private val calcRoundUpInteractor = mock(CalcRoundUpInteractor::class.java)
    private val listAccountsInteractor = mock(ListAccountsInteractor::class.java)
    private val localeProvider: Provider<*> = mock(Provider::class.java)
    private val stringSupplier = mock(StringSupplier::class.java)
    private val amountFormatter = mock(AmountFormatter::class.java)

    private val account1 = Account(
        "1",        "111",
        "cat1",
        Currency.getInstance("GBP"),
        "100".toBigDecimal()
    )

    private val account2 = Account(
        "2",
        "222",
        "cat2",
        Currency.getInstance("EUR"),
        "200".toBigDecimal()
    )

    private val quarter = "0.25".toBigDecimal()

    val state = SavedStateHandle()

    @Suppress("UNCHECKED_CAST")
    private val viewModel by lazy {
        AccountsViewModel(state).apply {
            // manual field and method injection
            _calcRoundUpInteractor = calcRoundUpInteractor
            _listAccountsInteractor = listAccountsInteractor
            _localeProvider = localeProvider as Provider<Locale>
            _stringSupplier = stringSupplier
            _amountFormatter = amountFormatter
            init()
        }
    }

    @Before
    fun beforeEach() {
        given(stringSupplier.get(R.string.roundUpInfo)).willReturn("Round up amount since %s")
        given(stringSupplier.get(R.string.no_account)).willReturn("No account")
        given(localeProvider.get()).willReturn(Locale.UK)
        given(amountFormatter.format(any(), any(), any())).willReturn("")
    }

    @Test
    fun `Initial interactions`() = runBlocking {
        given(listAccountsInteractor.execute()).willReturn(listOf(account1))
        given(calcRoundUpInteractor.execute(any(),any())).willReturn(quarter)

        viewModel // instantiate

        then(listAccountsInteractor).should(timeout(TIMEOUT)).execute()
        then(listAccountsInteractor).shouldHaveNoMoreInteractions()

        then(calcRoundUpInteractor).should(timeout(TIMEOUT))
            .execute(account1.id, LocalDate.now().minusWeeks(1))
        then(calcRoundUpInteractor).shouldHaveNoMoreInteractions()

        Unit
    }

    @Test()
    fun testFirstAccountIsSelectedByDefault() = runBlocking {
        given(listAccountsInteractor.execute()).willReturn(listOf(account1, account2))
        given(calcRoundUpInteractor.execute(any(),any())).willReturn(quarter)

        var position: Int? = null
        viewModel.selectedAccountPosition.observeForever { position = it }

        assertThat(position, equalTo(0))
    }

    @Test
    fun testRestoringSelectedAccount() = runBlocking {
        given(listAccountsInteractor.execute()).willReturn(listOf(account1, account2))
        state.accountId = account2.id

        given(calcRoundUpInteractor.execute(any(),any())).willReturn(quarter)

        viewModel // instantiate

        var position: Int? = null
        viewModel.selectedAccountPosition.observeForever { position = it }

        delay(100)
        assertThat(position, equalTo(1))
    }

    @Test
    fun `Given RoundUpAmount is positive Then Transfer Command will be enabled`() = runBlocking {
        given(listAccountsInteractor.execute()).willReturn(listOf(account1))
        given(calcRoundUpInteractor.execute(any(),any())).willReturn(quarter)

        var enabled: Boolean? = null
        viewModel.transferCommandEnabled.observeForever { enabled = it }

        delay(100)
        assertThat(enabled, `is`(true))
    }
}
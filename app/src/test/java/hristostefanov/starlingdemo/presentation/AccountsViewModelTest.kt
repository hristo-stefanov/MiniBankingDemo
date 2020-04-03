package hristostefanov.starlingdemo.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
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
class AccountsViewModelTest: BaseViewModelTest() {
    private val calcRoundUpInteractor = mock(CalcRoundUpInteractor::class.java)
    private val listAccountsInteractor = mock(ListAccountsInteractor::class.java)
    private val localeProvider: Provider<*> = mock(Provider::class.java)
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
        AccountsViewModel(SavedStateHandle()).apply {
            // manual field and method injection
            _calcRoundUpInteractor = calcRoundUpInteractor
            _listAccountsInteractor = listAccountsInteractor
            _localeProvider = localeProvider as Provider<Locale>
            _stringSupplier = stringSupplier
            _amountFormatter = amountFormatter
            init()
        }
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
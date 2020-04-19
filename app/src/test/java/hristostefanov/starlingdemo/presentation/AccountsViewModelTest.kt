package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import hristostefanov.starlingdemo.R
import hristostefanov.starlingdemo.any
import hristostefanov.starlingdemo.business.entities.Account
import hristostefanov.starlingdemo.business.interactors.CalcRoundUpInteractor
import hristostefanov.starlingdemo.business.interactors.DataSourceChangedEvent
import hristostefanov.starlingdemo.business.interactors.ListAccountsInteractor
import hristostefanov.starlingdemo.presentation.AccountsViewModel.Companion.accountId
import hristostefanov.starlingdemo.presentation.dependences.AmountFormatter
import hristostefanov.starlingdemo.ui.AccountsFragmentDirections
import hristostefanov.starlingdemo.util.StringSupplier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock
import org.mockito.Mockito.timeout
import java.time.LocalDate
import java.util.*
import javax.inject.Provider
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val TIMEOUT = 100L

//@ExperimentalCoroutinesApi
//@ObsoleteCoroutinesApi
class AccountsViewModelTest : BaseViewModelTest() {
    private val calcRoundUpInteractor = mock(CalcRoundUpInteractor::class.java)
    private val listAccountsInteractor = mock(ListAccountsInteractor::class.java)
    private val localeProvider: Provider<*> = mock(Provider::class.java)
    private val stringSupplier = mock(StringSupplier::class.java)
    private val amountFormatter = mock(AmountFormatter::class.java)

    // TODO mocking a type that do not own
    private val eventBus = mock(EventBus::class.java)

    // TODO mocking a type that do not own
    private val navigationChannel = mock(Channel::class.java) as Channel<Navigation>

    private val account1 = Account(
        "1", "111",
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

    private val state = SavedStateHandle()

    @Suppress("UNCHECKED_CAST")
    private val viewModel by lazy {
        AccountsViewModel(state).also {
            // manual field and method injection
            it._calcRoundUpInteractor = calcRoundUpInteractor
            it._listAccountsInteractor = listAccountsInteractor
            it._localeProvider = localeProvider as Provider<Locale>
            it._stringSupplier = stringSupplier
            it._amountFormatter = amountFormatter
            it.eventBus = eventBus
            it.navigationChannel = navigationChannel
            it.init()
        }
    }

    @Before
    fun beforeEach() = runBlocking {
        given(stringSupplier.get(R.string.roundUpInfo)).willReturn("Round up amount since %s")
        given(stringSupplier.get(R.string.no_account)).willReturn("No account")
        given(localeProvider.get()).willReturn(Locale.UK)
        given(amountFormatter.format(any(), any(), any())).willReturn("")
        given(calcRoundUpInteractor.execute(any(), any())).willReturn(quarter)
        given(listAccountsInteractor.execute()).willReturn(listOf(account1))
        Unit
    }


    @Test
    fun `Initial interactions`() = runBlocking {
        viewModel // instantiate

        then(eventBus).should().register(viewModel)
        then(listAccountsInteractor).should(timeout(TIMEOUT)).execute()
        then(listAccountsInteractor).shouldHaveNoMoreInteractions()
        then(calcRoundUpInteractor).should(timeout(TIMEOUT))
            .execute(account1.id, LocalDate.now().minusWeeks(1))
        then(calcRoundUpInteractor).shouldHaveNoMoreInteractions()

        Unit
    }

    @Test
    fun `Interactions on DataSourceChangedEvent`() = runBlocking {
        viewModel.onDataSourceChanged(DataSourceChangedEvent())

        then(listAccountsInteractor).should(timeout(TIMEOUT).times(2)).execute()
        then(calcRoundUpInteractor).should(timeout(TIMEOUT).times(2))
            .execute(account1.id, LocalDate.now().minusWeeks(1))
        Unit
    }

    @Test
    fun `OnCleared interactions`() = runBlocking {
        viewModel.onCleared()
        then(eventBus).should().unregister(viewModel)
    }


    @Test
    fun onTransferCommand() = runBlocking {
        // wait for the command to get enabled
        suspendCoroutine<Unit> {continuation ->
            viewModel.transferCommandEnabled.observeForever {
                if (it)
                    continuation.resume(Unit)
            }
        }

        viewModel.onTransferCommand()

        then(navigationChannel).should(timeout(TIMEOUT)).send(
            Navigation.Forward(
                AccountsFragmentDirections.actionToSavingsGoalsDestination(
                    account1.id,
                    account1.currency, quarter
                )
            )
        )
    }


    @Test()
    fun testFirstAccountIsSelectedByDefault() = runBlocking {
        given(listAccountsInteractor.execute()).willReturn(listOf(account1, account2))
        @Suppress("UNCHECKED_CAST")
        val observer = mock(Observer::class.java) as Observer<Int>

        viewModel.selectedAccountPosition.observeForever(observer)

        then(observer).should(timeout(TIMEOUT)).onChanged(0)
    }

    @Test
    fun testRestoringSelectedAccount() = runBlocking {
        val accounts = listOf(account1, account2)
        given(listAccountsInteractor.execute()).willReturn(accounts)
        state.accountId = account2.id
        @Suppress("UNCHECKED_CAST")
        val observer = mock(Observer::class.java) as Observer<Int>

        viewModel.selectedAccountPosition.observeForever(observer)

        then(observer).should(timeout(TIMEOUT)).onChanged(accounts.indexOf(account2))
    }



    @Test
    fun `Given RoundUpAmount is positive Then Transfer Command will be enabled`() = runBlocking {
        @Suppress("UNCHECKED_CAST")
        val observer = mock(Observer::class.java) as Observer<Boolean>

        viewModel.transferCommandEnabled.observeForever(observer)

        then(observer).should(timeout(TIMEOUT)).onChanged(true)
    }
}
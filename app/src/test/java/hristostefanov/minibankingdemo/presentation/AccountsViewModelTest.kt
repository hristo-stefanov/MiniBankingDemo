package hristostefanov.minibankingdemo.presentation

import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import hristostefanov.minibankingdemo.R
import hristostefanov.minibankingdemo.any
import hristostefanov.minibankingdemo.business.entities.Account
import hristostefanov.minibankingdemo.business.interactors.CalcRoundUpInteractor
import hristostefanov.minibankingdemo.business.interactors.DataSourceChangedEvent
import hristostefanov.minibankingdemo.business.interactors.ListAccountsInteractor
import hristostefanov.minibankingdemo.presentation.AccountsViewModel.Companion.accountId
import hristostefanov.minibankingdemo.presentation.dependences.AmountFormatter
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.ui.AccountsFragmentDirections
import hristostefanov.minibankingdemo.util.StringSupplier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.*
import java.time.LocalDate
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val TIMEOUT = 100L

class AccountsViewModelTest : BaseViewModelTest() {
    private val calcRoundUpInteractor = mock(CalcRoundUpInteractor::class.java)
    private val listAccountsInteractor = mock(ListAccountsInteractor::class.java)
    private val stringSupplier = mock(StringSupplier::class.java)
    private val amountFormatter = mock(AmountFormatter::class.java)
    private val tokenStore = mock(TokenStore::class.java)

    private val eventBus = spy(EventBus::class.java)
    @Suppress("UNCHECKED_CAST")
    private val navigationChannel = spy(Channel::class.java) as Channel<Navigation>

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

    @Before
    fun beforeEach() = runBlocking {
        given(stringSupplier.get(R.string.roundUpInfo)).willReturn("Round up amount since %s")
        given(stringSupplier.get(R.string.no_account)).willReturn("No account")
        given(amountFormatter.format(any(), any())).willReturn("")
        given(calcRoundUpInteractor.execute(any(), any())).willReturn(quarter)
        given(listAccountsInteractor.execute()).willReturn(listOf(account1))
        given(tokenStore.token).willReturn("token")
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
    fun `Data source changed`() = runBlocking {
        viewModel.onDataSourceChanged(DataSourceChangedEvent())

        then(listAccountsInteractor).should(timeout(TIMEOUT).times(2)).execute()
        then(calcRoundUpInteractor).should(timeout(TIMEOUT).times(2))
            .execute(account1.id, LocalDate.now().minusWeeks(1))
        Unit
    }

    @Test
    fun `View model cleared`() {
        viewModel.onCleared()
        then(eventBus).should().unregister(viewModel)
    }


    @Test
    fun `Transfer command selected`() = runBlocking {
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


    @Test
    fun `First Account Is Selected By Default`() = runBlocking {
        given(listAccountsInteractor.execute()).willReturn(listOf(account1, account2))
        @Suppress("UNCHECKED_CAST")
        val observer = spy(Observer::class.java) as Observer<Int>

        viewModel.selectedAccountPosition.observeForever(observer)

        then(observer).should(timeout(TIMEOUT)).onChanged(0)
    }

    @Test
    fun `Restoring Selected Account`() = runBlocking {
        val accounts = listOf(account1, account2)
        given(listAccountsInteractor.execute()).willReturn(accounts)
        state.accountId = account2.id
        @Suppress("UNCHECKED_CAST")
        val observer = spy(Observer::class.java) as Observer<Int>

        viewModel.selectedAccountPosition.observeForever(observer)

        then(observer).should(timeout(TIMEOUT)).onChanged(accounts.indexOf(account2))
    }



    @Test
    fun `GIVEN RoundUpAmount is positive THEN Transfer Command will be enabled`() {
        @Suppress("UNCHECKED_CAST")
        val observer = spy(Observer::class.java) as Observer<Boolean>

        viewModel.transferCommandEnabled.observeForever(observer)

        then(observer).should(timeout(TIMEOUT)).onChanged(true)
    }
}
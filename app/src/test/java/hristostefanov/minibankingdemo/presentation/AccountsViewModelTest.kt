package hristostefanov.minibankingdemo.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import hristostefanov.minibankingdemo.CoroutinesTestRule
import hristostefanov.minibankingdemo.R
import hristostefanov.minibankingdemo.any
import hristostefanov.minibankingdemo.business.entities.Account
import hristostefanov.minibankingdemo.business.interactors.CalcRoundUpInteractor
import hristostefanov.minibankingdemo.business.interactors.DataSourceChangedEvent
import hristostefanov.minibankingdemo.business.interactors.ListAccountsInteractor
import hristostefanov.minibankingdemo.presentation.dependences.AmountFormatter
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.ui.AccountsFragmentDirections
import hristostefanov.minibankingdemo.util.StringSupplier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.greenrobot.eventbus.EventBus
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.*
import java.time.LocalDate
import java.util.*

private const val TIMEOUT = 100L

@ExperimentalCoroutinesApi
class AccountsViewModelTest {
    private val calcRoundUpInteractor = mock(CalcRoundUpInteractor::class.java)
    private val listAccountsInteractor = mock(ListAccountsInteractor::class.java)
    private val stringSupplier = mock(StringSupplier::class.java)
    private val amountFormatter = mock(AmountFormatter::class.java)
    private val tokenStore = mock(TokenStore::class.java)

    @get:Rule
    val coroutineTestRule = CoroutinesTestRule()

    // NOTE: needed for proper testing of Architecture Components -
    // makes background tasks execute synchronously.
    // More importantly, provides TaskExecutor#isMainThread implementation which always return `true`
    // thus avoiding exceptions in LiveData's observe* methods.
    @get:Rule
    val rule = InstantTaskExecutorRule()


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
    fun `Initial interactions`() = coroutineTestRule.testDispatcher.runBlockingTest {
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
    fun `Data source changed`() = coroutineTestRule.testDispatcher.runBlockingTest {
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
    fun `Transfer command selected`() = coroutineTestRule.testDispatcher.runBlockingTest {
        // wait for the command to get enabled
        viewModel.transferCommandEnabled.first()

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
    fun `First Account Is Selected By Default`() = coroutineTestRule.testDispatcher.runBlockingTest {
        given(listAccountsInteractor.execute()).willReturn(listOf(account1, account2))

        val position = viewModel.selectedAccountPosition.first()

        assertThat(position).isEqualTo(0)
    }

    @Test
    fun `Restoring Selected Account`() = coroutineTestRule.testDispatcher.runBlockingTest {
        val accounts = listOf(account1, account2)
        given(listAccountsInteractor.execute()).willReturn(accounts)
        state[ACCOUNT_ID_KEY] = account2.id

        val position = viewModel.selectedAccountPosition.first()

        assertThat(position).isEqualTo(accounts.indexOf(account2))
    }


    @Test
    fun `GIVEN RoundUpAmount is positive THEN Transfer Command will be enabled`() = coroutineTestRule.testDispatcher.runBlockingTest {
        val isEnabled = viewModel.transferCommandEnabled.first()

        assertThat(isEnabled).isTrue()
    }
}
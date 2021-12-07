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
import hristostefanov.minibankingdemo.util.SessionRegistry
import hristostefanov.minibankingdemo.util.SessionComponent
import hristostefanov.minibankingdemo.util.StringSupplier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.greenrobot.eventbus.EventBus
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import java.time.LocalDate
import java.util.*

@ExperimentalCoroutinesApi
class AccountsViewModelTest {
    private val calcRoundUpInteractor = mock(CalcRoundUpInteractor::class.java)
    private val listAccountsInteractor = mock(ListAccountsInteractor::class.java)
    private val stringSupplier = mock(StringSupplier::class.java)
    private val amountFormatter = mock(AmountFormatter::class.java)
    private val tokenStore = mock(TokenStore::class.java)
    private val sessionRegistry = mock(SessionRegistry::class.java)
    private val sessionComponent = mock(SessionComponent::class.java)

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
    private val half = "0.5".toBigDecimal()

    private val state = SavedStateHandle()

    @Suppress("UNCHECKED_CAST")
    private val viewModel by lazy {
        AccountsViewModel(
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

    @Before
    fun beforeEach() = coroutineTestRule.testDispatcher.runBlockingTest {
        given(sessionRegistry.sessionComponent).willReturn(sessionComponent)
        given(sessionComponent.calcRoundUpInteractor).willReturn(calcRoundUpInteractor)
        given(sessionComponent.listAccountsInteractor).willReturn(listAccountsInteractor)

        given(stringSupplier.get(R.string.roundUpInfo)).willReturn("Round up amount since %s")
        given(stringSupplier.get(R.string.no_account)).willReturn("No account")
        given(amountFormatter.format(any(), any())).willReturn("")
        given(calcRoundUpInteractor.execute(any(), any())).willReturn(quarter)
        given(listAccountsInteractor.execute()).willReturn(listOf(account1))
        given(tokenStore.refreshToken).willReturn("token")
    }

    @Test
    fun `Initial interactions`() = coroutineTestRule.testDispatcher.runBlockingTest {
        viewModel // instantiate

        then(eventBus).should().register(viewModel)
        then(listAccountsInteractor).should().execute()
        then(listAccountsInteractor).shouldHaveNoMoreInteractions()
        then(calcRoundUpInteractor).should()
            .execute(account1.id, LocalDate.now().minusWeeks(1))
        then(calcRoundUpInteractor).shouldHaveNoMoreInteractions()
    }

    @Test
    fun `Should update outputs when data source changes`() =
        coroutineTestRule.testDispatcher.runBlockingTest {
            // get the first data source set
            viewModel

            // provide a second data source set
            given(calcRoundUpInteractor.execute(any(), any())).willReturn(half)
            given(listAccountsInteractor.execute()).willReturn(listOf(account2))
            given(amountFormatter.format(half, "EUR")).willReturn("€0.50")

            viewModel.onDataSourceChanged(DataSourceChangedEvent())

            assertThat(viewModel.accountList.value[0].number).isEqualTo("222")
            assertThat(viewModel.roundUpAmountText.value).isEqualTo("€0.50")
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

        then(navigationChannel).should().send(
            Navigation.Forward(
                AccountsFragmentDirections.actionToSavingsGoalsDestination(
                    account1.id,
                    account1.currency, quarter
                )
            )
        )
    }

    @Test
    fun `First Account should be selected by default`() =
        coroutineTestRule.testDispatcher.runBlockingTest {
            given(listAccountsInteractor.execute()).willReturn(listOf(account1, account2))

            val position = viewModel.selectedAccountPosition.first()

            assertThat(position).isEqualTo(0)
        }

    @Test
    fun `Should restore Account selection`() = coroutineTestRule.testDispatcher.runBlockingTest {
        val accounts = listOf(account1, account2)
        given(listAccountsInteractor.execute()).willReturn(accounts)
        state[ACCOUNT_ID_KEY] = account2.id

        val position = viewModel.selectedAccountPosition.first()

        assertThat(position).isEqualTo(accounts.indexOf(account2))
    }


    @Test
    fun `Transfer Command should be enabled when RoundUpAmount is positive`() =
        coroutineTestRule.testDispatcher.runBlockingTest {
            val isEnabled = viewModel.transferCommandEnabled.first()

            assertThat(isEnabled).isTrue()
        }
}
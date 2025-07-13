package hristostefanov.minibankingdemo.presentation

import androidx.lifecycle.SavedStateHandle
import hristostefanov.minibankingdemo.MainDispatcherRule
import hristostefanov.minibankingdemo.R
import hristostefanov.minibankingdemo.any
import hristostefanov.minibankingdemo.business.interactors.CalcRoundUpInteractor
import hristostefanov.minibankingdemo.presentation.dependences.AmountFormatter
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.ui.AccountsFragmentDirections
import hristostefanov.minibankingdemo.usecase.output.AccountsAndRoundUpsSummary
import hristostefanov.minibankingdemo.util.LoginSessionComponent
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import hristostefanov.minibankingdemo.util.StringSupplier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.greenrobot.eventbus.EventBus
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import java.time.OffsetDateTime
import java.util.Currency
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class AccountsViewModelTest {
    private val calcRoundUpInteractor = mock(CalcRoundUpInteractor::class.java)
    private val stringSupplier = mock(StringSupplier::class.java)
    private val amountFormatter = mock(AmountFormatter::class.java)
    private val tokenStore = mock(TokenStore::class.java)
    private val loginSessionRegistry = mock(LoginSessionRegistry::class.java)
    private val loginSessionComponent = mock(LoginSessionComponent::class.java)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val eventBus = spy(EventBus::class.java)

    @Suppress("UNCHECKED_CAST")
    private val navigationChannel = spy(Channel::class.java) as Channel<Navigation>

    private val userInterface = UserInterfaceImpl(navigationChannel)

    private val account1 = AccountsAndRoundUpsSummary.Item(
        "1",
        "111",
        Currency.getInstance("GBP"),
        "0.10".toBigDecimal(),
        "100".toBigDecimal()
    )

    private val account2 = AccountsAndRoundUpsSummary.Item(
        "2",
        "222",
        Currency.getInstance("EUR"),
        "0.20".toBigDecimal(),
        "200".toBigDecimal()
    )

    private val quarter = "0.25".toBigDecimal()
    private val half = "0.5".toBigDecimal()

    private val state = SavedStateHandle()

    @Suppress("UNCHECKED_CAST")
    private val viewModel: AccountsViewModel by lazy {
        AccountsViewModel(
            state,
            Locale.UK,
            stringSupplier,
            amountFormatter,
            navigationChannel,
            tokenStore,
            loginSessionRegistry,
            userInterface
        )
    }

    @Before
    fun beforeEach() = runTest {
        given(loginSessionRegistry.component).willReturn(loginSessionComponent)
        given(stringSupplier.get(R.string.roundUpInfo)).willReturn("")
        given(amountFormatter.format(any(), any())).willReturn("")
    }

    // TODO Error state, Empty state

    @Test
    fun `Should format summary`() = runTest {
        given(stringSupplier.get(R.string.roundUpInfo)).willReturn("Round up amount since %s")
        given(amountFormatter.format("100".toBigDecimal(), "GBP")).willReturn("£100")
        given(amountFormatter.format("0.10".toBigDecimal(), "GBP")).willReturn("£0.10")

        userInterface.presentSummary(
            AccountsAndRoundUpsSummary(
                OffsetDateTime.parse("2025-06-01T00:00Z"), listOf(account1)
            )
        )

        viewModel

        assertThat(viewModel.accountList.value[0]).isEqualTo(DisplayAccount("111", "GBP", "£100"))
        assertThat(viewModel.roundUpAmountText.value).isEqualTo("£0.10")
        assertThat(viewModel.roundUpInfo.value).isEqualTo("Round up amount since 1 Jun 2025")
    }

    @Test
    fun `Should update outputs when summary changes`() = runTest {
        userInterface.presentSummary(
            AccountsAndRoundUpsSummary(
                OffsetDateTime.parse("2025-06-01T00:00Z"), listOf(account1)
            )
        )

        // get the first summary
        viewModel

        // provide another summary


        userInterface.presentSummary(
            AccountsAndRoundUpsSummary(
                OffsetDateTime.parse("2025-07-07T00:00Z"), listOf(account2)
            )
        )

        assertThat(viewModel.accountList.value[0].number).isEqualTo("222")
    }

    @Test
    fun `Should handle Transfer command`() = runTest {
        userInterface.presentSummary(
            AccountsAndRoundUpsSummary(
                OffsetDateTime.parse("2025-06-01T00:00Z"), listOf(account1)
            )
        )

        // wait for the command to get enabled
        viewModel.transferCommandEnabled.first()

        viewModel.onTransferCommand()

        then(navigationChannel).should().send(
            Navigation.Forward(
                AccountsFragmentDirections.actionToSavingsGoalsDestination(
                    account1.accountId,
                    account1.currency,
                    account1.roundUp
                )
            )
        )
    }

    @Test
    fun `Initially should select the first account`() = runTest {
        userInterface.presentSummary(
            AccountsAndRoundUpsSummary(
                OffsetDateTime.parse("2025-06-01T00:00Z"), listOf(
                    account1,
                    account2
                )
            )
        )

        val position = viewModel.selectedAccountPosition.first()

        assertThat(position).isEqualTo(0)

    }

    @Test
    fun `Should restore Account selection`() = runTest {
        state[ACCOUNT_ID_KEY] = account2.accountId

        val accounts = listOf(
            account1,
            account2
        )

        userInterface.presentSummary(
            AccountsAndRoundUpsSummary(
                OffsetDateTime.parse("2025-06-01T00:00Z"),
                accounts
            )
        )
        val position = viewModel.selectedAccountPosition.first()

        assertThat(position).isEqualTo(accounts.indexOf(account2))
    }

    @Test
    fun `Should enable Transfer command when an account is selected`() = runTest {
        userInterface.presentSummary(
            AccountsAndRoundUpsSummary(
                OffsetDateTime.parse("2025-06-01T00:00Z"), listOf(account1)
            )
        )

        val isEnabled = viewModel.transferCommandEnabled.first()

        assertThat(isEnabled).isTrue()
    }
}
package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.acceptancetest.technical.TestApp
import hristostefanov.minibankingdemo.presentation.AccountsViewModel
import hristostefanov.minibankingdemo.presentation.Navigation
import hristostefanov.minibankingdemo.util.NavigationChannel
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import javax.inject.Inject


class EncourageUsersToSaveMoneySteps {

    @Inject
    internal lateinit var automation: PresentationTestAutomation

    @Inject @NavigationChannel
    internal lateinit var navigationChannel: Channel<Navigation>

    private lateinit var accountsViewModel: AccountsViewModel

    @Before("@steps:encourageUsersToSaveMoney")
    fun beforeEachScenario() {
        TestApp.component.inject(this)

        i_am_logged_in()
    }

    private fun i_am_logged_in() {
        automation.correctRefreshTokenIs("correctToken")

        automation.openLoginScreen().run {
            onAccessTokenChanged("correctToken")
            onAcceptCommand()
        }

        // consume back navigation event
        runBlocking {
            navigationChannel.receive()
        }
    }

    @Given("the calculated round-up for my account is {double}")
    fun the_calculated_round_up_for_my_account_is(amount: Double) {
        automation.accountIn("GBP")
        automation.calculatedRoundUpIs(amount.toBigDecimal())
    }

    @When("I view this account")
    fun i_view_this_account() {
        accountsViewModel = automation.openAccountScreen()
        accountsViewModel.onAccountSelectionChanged(0)
    }

    @Then("I should be offered to save {string}")
    fun i_should_be_offered_to_save(offer: String) {
        assertThat(accountsViewModel.roundUpAmountText.value, `is`(offer))
    }

    @Then("I should be able to transfer the offered amount to a savings goal")
    fun i_should_be_able_to_transfer_the_offered_amount_to_a_savings_goal() {
        assertThat(accountsViewModel.transferCommandEnabled.value, `is`(true))
    }
}

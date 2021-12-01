package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.acceptancetest.technical.TestApp
import hristostefanov.minibankingdemo.presentation.AccessTokenViewModel
import hristostefanov.minibankingdemo.presentation.AccountsViewModel
import hristostefanov.minibankingdemo.presentation.Navigation
import hristostefanov.minibankingdemo.ui.AccountsFragmentDirections
import hristostefanov.minibankingdemo.util.NavigationChannel
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import javax.inject.Inject

val CORRECT_TOKEN = "correctToken"

class LoginSteps {

    init {
        TestApp.component.inject(this)
    }

    @Inject
    lateinit var automation: PresentationTestAutomation

    @Inject
    @NavigationChannel
    lateinit var navigationChannel: Channel<Navigation>

    // TODO use a map to share state accross step files?
    private lateinit var accountsViewModel: AccountsViewModel
    private lateinit var accessTokenViewModel: AccessTokenViewModel

    @Before
    fun beforeEachScenario() {
        automation.correctAuthTokenIs(CORRECT_TOKEN)
    }

    @Given("I am not logged in")
    fun i_am_not_logged_in() {
        // empty
    }

    @When("I try to access {string}")
    fun i_try_to_access(service: String) {
        // TODO the rest of the screens
        val vm = automation.openAccountScreen()
    }

    @Then("I should be asked to login")
    fun i_should_be_asked_to_login() {
        val nav = runBlocking {
            navigationChannel.receive()
        }

        assertThat(nav).isEqualTo(Navigation.Forward(AccountsFragmentDirections.toAccessTokenDestination()))
    }

    @Given("I'm asked to login to access Accounts")
    fun i_m_asked_to_login_to_access_accounts() {
        automation.accountIn("GBP")
        automation.calculatedRoundUpIs("3.14".toBigDecimal())
        accountsViewModel = automation.openAccountScreen()
        accessTokenViewModel = automation.openLoginScreen()
    }

    @When("I provided correct credentials")
    fun i_provided_correct_credentials() {
        accessTokenViewModel.onAccessTokenChanged(CORRECT_TOKEN)
        accessTokenViewModel.onAcceptCommand()
    }

    @Then("I should access the online banking")
    fun i_should_access_the_online_banking() {
        val nav = runBlocking {
            // consume the navigation to log in
            navigationChannel.receive()

            // consume the backwards navigation from the login screen
            navigationChannel.receive()
        }

        assertThat(nav).isEqualTo(Navigation.Backward)
        assertThat(accountsViewModel.roundUpAmountText.value).isEqualTo("£3.14")
    }
}
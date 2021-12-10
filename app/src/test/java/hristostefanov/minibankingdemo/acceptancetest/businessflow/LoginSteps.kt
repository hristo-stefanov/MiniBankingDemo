package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.acceptancetest.technical.TestApp
import hristostefanov.minibankingdemo.presentation.AccessTokenViewModel
import hristostefanov.minibankingdemo.presentation.AccountsViewModel
import hristostefanov.minibankingdemo.presentation.Navigation
import hristostefanov.minibankingdemo.ui.AccountsFragmentDirections
import hristostefanov.minibankingdemo.util.NavigationChannel
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import javax.inject.Inject

val CORRECT_REFRESH_TOKEN = "correctToken"

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

    @Given("I am registered for online banking")
    fun online_banking() {
        automation.correctRefreshTokenIs(CORRECT_REFRESH_TOKEN)
        automation.accountIn("GBP")
    }


    @Given("I'm asked to login to access Accounts")
    fun i_m_asked_to_login_to_access_accounts() {
        accountsViewModel = automation.openAccountScreen()
        accessTokenViewModel = automation.openLoginScreen()
    }

    @When("I provided correct credentials")
    fun i_provided_correct_credentials() {
        accessTokenViewModel.onAccessTokenChanged(CORRECT_REFRESH_TOKEN)
        accessTokenViewModel.onAcceptCommand()
    }

    @Then("credentials screen will disappear")
    fun credentials_screen_will_disappear() {
        val nav = runBlocking {
            // consume the navigation to log in
            navigationChannel.receive()

            // consume the backwards navigation from the login screen
            navigationChannel.receive()
        }

        assertThat(nav).isEqualTo(Navigation.Backward)
    }

    @Then("I should be given access to the online banking")
    fun i_should_access_the_online_banking() {
        assertThat(accountsViewModel.accountList.value).isNotEmpty
    }

    @When("I log out")
    fun i_log_out() {
        accountsViewModel = automation.openAccountScreen()
        accountsViewModel.onLogout()

        // restarting navigation is supposed reopen the screen
        accountsViewModel = automation.openAccountScreen()
    }

    @Then("my account information should disappear")
    fun my_account_information_should_disappear() {
        val nav = runBlocking {
            navigationChannel.receive()
        }

        assertThat(nav).isEqualTo(Navigation.Restart)
    }

    @Given("I was logged before exiting the app")
    fun i_was_logged_before_exiting_the_app() {
        automation.savedRefreshTokenIs(CORRECT_REFRESH_TOKEN)
    }

    @When("I launch the app")
    fun i_launch_the_app() {
        // this is main screen
        accountsViewModel = automation.openAccountScreen()
        // the main screen should navigate to this screen
        // it will auto-login or ask for credential
        accessTokenViewModel = automation.openLoginScreen()

    }

}
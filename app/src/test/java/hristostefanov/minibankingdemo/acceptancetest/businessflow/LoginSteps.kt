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

private val CORRECT_REFRESH_TOKEN = "correctToken"

class LoginSteps {
    // TODO use a map to share state accross step files?
    private lateinit var accountsViewModel: AccountsViewModel
    private lateinit var accessTokenViewModel: AccessTokenViewModel

    @Inject
    lateinit var automation: PresentationTestAutomation

    @Inject
    @NavigationChannel
    lateinit var navigationChannel: Channel<Navigation>

    @Before("@steps:login")
    fun beforeEachScenario() {
        TestApp.component.inject(this)
    }

    @Given("I am not logged in")
    fun i_am_not_logged_in() {
        // empty
    }

    @When("I try to access my bank accounts")
    fun i_try_to_access_my_bank_accounts() {
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
        // create a default account to be able to verify access to online banking is given
        // this works ok for the purpose of loggin related scenarios
        automation.accountIn("GBP")
    }


    @Given("I'm asked to login to access my accounts")
    fun i_m_asked_to_login_to_access_my_accounts() {
        accountsViewModel = automation.openAccountScreen()
        runBlocking {
            // consume the navigation to log in
            navigationChannel.receive()
        }
        accessTokenViewModel = automation.openLoginScreen()
    }

    @When("I provide correct credentials")
    fun i_provide_correct_credentials() {
        accessTokenViewModel.onAccessTokenChanged(CORRECT_REFRESH_TOKEN)
        accessTokenViewModel.onAcceptCommand()
    }

    @Then("I should be given access to my accounts")
    fun i_should_access_the_online_banking() {
        val nav = runBlocking {
            // consume the backwards navigation from the login screen
            navigationChannel.receive()
        }

        assertThat(nav).isEqualTo(Navigation.Backward)
        // check if the default account can be accessed
        assertThat(accountsViewModel.accountList.value.first().currency).isEqualTo("GBP")
    }

    @Given("I am seeing my account information")
    fun i_am_seeing_my_account_information() {
        automation.openLoginScreen().run {
            onAccessTokenChanged(CORRECT_REFRESH_TOKEN)
            onAcceptCommand()
        }

        // consume back navigation event
        runBlocking {
            navigationChannel.receive()
        }

        accountsViewModel = automation.openAccountScreen()
    }

    @When("I log out")
    fun i_log_out() {
        accountsViewModel.onLogout()

        // restarting navigation reopens the screen
        accountsViewModel = automation.openAccountScreen()
    }

    @Then("my account information should be hidden")
    fun my_account_information_should_be_hidden() {
        val nav = runBlocking {
            navigationChannel.receive()
        }

        assertThat(nav).isEqualTo(Navigation.Restart)
    }

    @Given("I was logged in before exiting the app")
    fun i_was_logged_in_before_exiting_the_app() {
        automation.savedRefreshTokenIs(CORRECT_REFRESH_TOKEN)
    }

    @When("I launch the app to access Accounts")
    fun i_launch_the_app_to_access_accounts() {
        accountsViewModel = automation.openAccountScreen()
        // Accounts screen should navigate to Login screen
        // which will auto-login or ask for credentials
        accessTokenViewModel = automation.openLoginScreen()

    }

    @Then("I should be logged in")
    fun i_should_be_logged_in() {
        // check if the default account can be accessed
        assertThat(accountsViewModel.accountList.value.first().currency).isEqualTo("GBP")
    }

}
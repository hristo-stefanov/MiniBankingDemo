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
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import javax.inject.Inject

private const val CORRECT_REFRESH_TOKEN = "correctToken"

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

        automation.correctRefreshTokenIs(CORRECT_REFRESH_TOKEN)

        // create a default account to be able to verify access to online banking is given
        // this works ok for the purpose of loggin related scenarios
        automation.accountIn("GBP")
    }


    @Given("I am not logged in")
    fun i_am_not_logged_in() {
        // empty
    }

    @When("I try to access my bank accounts")
    fun i_try_to_access_my_bank_accounts() {
        val vm = automation.openAccountScreen()
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

}
package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.acceptancetest.technical.TestApp
import hristostefanov.minibankingdemo.presentation.LoginViewModel
import hristostefanov.minibankingdemo.presentation.AccountsViewModel
import hristostefanov.minibankingdemo.presentation.Navigation
import hristostefanov.minibankingdemo.util.NavigationChannel
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import javax.inject.Inject

private const val CORRECT_ACCESS_TOKEN = "correctToken"

class LoginSteps {
    private lateinit var accountsViewModel: AccountsViewModel
    private lateinit var loginViewModel: LoginViewModel

    @Inject
    internal lateinit var automation: PresentationTestAutomation

    @Inject
    @NavigationChannel
    internal lateinit var navigationChannel: Channel<Navigation>

    @Before("@steps:login")
    fun beforeEachScenario() {
        TestApp.component.inject(this)

        automation.correctAccessTokenIs(CORRECT_ACCESS_TOKEN)

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
        runTest {
            accountsViewModel = automation.openAccountScreen()

            // consume the navigation to log in
            navigationChannel.receive()

            loginViewModel = automation.openLoginScreen()
        }
    }

    @When("I provide correct credentials")
    fun i_provide_correct_credentials() {
        loginViewModel.onAccessTokenChanged(CORRECT_ACCESS_TOKEN)
        loginViewModel.onAcceptCommand()
    }

    @Then("I should be given access to my accounts")
    fun i_should_access_the_online_banking() {
        runTest {
            // consume the backwards navigation from the login screen
            val nav = navigationChannel.receive()

            assertThat(nav).isEqualTo(Navigation.Backward)
            // check if the default account can be accessed
            assertThat(accountsViewModel.accountList.value.first().currency).isEqualTo("GBP")
        }

    }
}
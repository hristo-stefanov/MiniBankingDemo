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
import org.assertj.core.api.Assertions
import javax.inject.Inject

class LogoutSteps {
    private lateinit var accountsViewModel: AccountsViewModel

    @Inject
    internal lateinit var automation: PresentationTestAutomation

    @Inject
    @NavigationChannel
    internal lateinit var navigationChannel: Channel<Navigation>

    @Before("@steps:logout")
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

    @Given("I am seeing my account information")
    fun i_am_seeing_my_account_information() {
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

        Assertions.assertThat(nav).isEqualTo(Navigation.Restart)
    }
}
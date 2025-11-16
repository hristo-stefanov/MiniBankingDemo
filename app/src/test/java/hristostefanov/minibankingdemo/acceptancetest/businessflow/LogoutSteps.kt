package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.acceptancetest.technical.TestApp
import hristostefanov.minibankingdemo.presentation.AccountsViewModel
import hristostefanov.minibankingdemo.presentation.MainCommand
import hristostefanov.minibankingdemo.util.MainCommandChannel
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions
import javax.inject.Inject

class LogoutSteps {
    private lateinit var accountsViewModel: AccountsViewModel

    @Inject
    internal lateinit var automation: PresentationTestAutomation

    @Inject
    @MainCommandChannel
    internal lateinit var mainCommandChannel: Channel<MainCommand>

    @Before("@steps:logout")
    fun beforeEachScenario() {
        TestApp.component.inject(this)
        i_am_logged_in()
    }

    private fun i_am_logged_in() = runTest {
        automation.correctAccessTokenIs("correctToken")

        automation.startUp()

        automation.openLoginScreen().run {
            onAccessTokenChanged("correctToken")
            onAcceptCommand()
        }

        // consume back navigation event
        mainCommandChannel.receive()
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
    fun my_account_information_should_be_hidden() = runTest {
        val nav = mainCommandChannel.receive()
        Assertions.assertThat(nav).isEqualTo(MainCommand.Restart)
    }
}
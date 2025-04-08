package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.acceptancetest.technical.TestApp
import hristostefanov.minibankingdemo.business.interactors.DataSourceChangedEvent
import hristostefanov.minibankingdemo.presentation.AccountsViewModel
import hristostefanov.minibankingdemo.presentation.LoginViewModel
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.test.advanceUntilIdle
import org.assertj.core.api.Assertions
import javax.inject.Inject
import kotlinx.coroutines.test.runTest

private const val CORRECT_ACCESS_TOKEN = "correctToken"
private const val INVALID_ACCESS_TOKEN = "invalidToken"

class AutoLoginSteps {
    private lateinit var accountsViewModel: AccountsViewModel

    @Inject
    lateinit var automation: PresentationTestAutomation

    @Before("@steps:autologin")
    fun beforeEachScenario() {
        TestApp.component.inject(this)

        automation.correctAccessTokenIs(CORRECT_ACCESS_TOKEN)

        // create a default account to be able to verify access to online banking is given
        // this works ok for the purpose of loggin related scenarios
        automation.accountIn("GBP")
    }

    @Given("I was logged in before exiting the app")
    fun i_was_logged_in_before_exiting_the_app() {
        automation.savedAccessTokenIs(CORRECT_ACCESS_TOKEN)
    }

    @When("I launch the app to access Accounts")
    fun i_launch_the_app_to_access_accounts() {
        accountsViewModel = automation.openAccountScreen()
    }

    @Then("I should be logged in")
    fun i_should_be_logged_in() = runTest {
        // TODO the scenario passes withou it?
        advanceUntilIdle()

        // check if the default account can be accessed
        Assertions.assertThat(accountsViewModel.accountList.value.first().currency).isEqualTo("GBP")
    }

    @Given("the app keeps an invalid token")
    fun the_app_keeps_an_invalid_token() {
        automation.savedAccessTokenIs(INVALID_ACCESS_TOKEN)
    }

    @Given("there is no internet connection")
    fun there_is_no_internet_connection() {
        automation.thereIsNoInternetConnection()
    }
}
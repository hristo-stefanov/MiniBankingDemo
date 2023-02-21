package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.acceptancetest.technical.TestApp
import hristostefanov.minibankingdemo.presentation.AccountsViewModel
import hristostefanov.minibankingdemo.presentation.LoginViewModel
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions
import javax.inject.Inject

private const val CORRECT_REFRESH_TOKEN = "correctToken"
private const val INVALID_REFRESH_TOKEN = "invalidToken"

class AutoLoginSteps {
    private lateinit var accountsViewModel: AccountsViewModel
    private lateinit var loginViewModel: LoginViewModel

    @Inject
    lateinit var automation: PresentationTestAutomation

    @Before("@steps:autologin")
    fun beforeEachScenario() {
        TestApp.component.inject(this)

        automation.correctRefreshTokenIs(CORRECT_REFRESH_TOKEN)

        // create a default account to be able to verify access to online banking is given
        // this works ok for the purpose of loggin related scenarios
        automation.accountIn("GBP")
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
        loginViewModel = automation.openLoginScreen()
    }

    @Then("I should be logged in")
    fun i_should_be_logged_in() {
        // check if the default account can be accessed
        Assertions.assertThat(accountsViewModel.accountList.value.first().currency).isEqualTo("GBP")
    }

    @Given("the app keeps an invalid token")
    fun the_app_keeps_an_invalid_token() {
        automation.savedRefreshTokenIs(INVALID_REFRESH_TOKEN)
    }
}
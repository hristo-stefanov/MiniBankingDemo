package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.business.interactors.shared.ShowAccountsAndRoundupsInteractor
import hristostefanov.minibankingdemo.business.interactors.shared.ShowAccountsAndRoundupsOutputBoundary
import hristostefanov.minibankingdemo.business.interactors.startup.StartupInteractor
import hristostefanov.minibankingdemo.business.interactors.startup.StartupOutputBoundary
import hristostefanov.minibankingdemo.any
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import io.cucumber.java.Before
import io.cucumber.java.PendingException
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock

class StartupSteps {
//    @Inject
//    @NavigationChannel
//    internal lateinit var navigationChannel: Channel<Navigation>

//    @Inject
//    internal lateinit var tokenStore: TokenStore

//

    private val tokenStore: TokenStore = mock()

    private val startupOutputBoundary: StartupOutputBoundary = mock()
    private val showAccountsAndRoundupsOutputBoundary: ShowAccountsAndRoundupsOutputBoundary =
        mock()

    private val showAccountsAndRoundupsInteractor =
        ShowAccountsAndRoundupsInteractor(showAccountsAndRoundupsOutputBoundary)
    private val startupInteractor =
        StartupInteractor(startupOutputBoundary, tokenStore, showAccountsAndRoundupsInteractor)


    @Before("@steps:startup")
    fun beforeEachScenario() {
//        TestApp.component.inject(this)
    }

    @Given("my login credentials have not been saved")
    fun my_login_credentials_have_not_been_saved() {
        given(tokenStore.token).willReturn("")
    }

    @When("I launch the app")
    fun i_launch_the_app() {
        startupInteractor.launchApp()
    }

    @Then("I should be prompted to submit my login credentials")
    fun i_should_be_prompted_to_submit_my_login_credentials() {
        then(startupOutputBoundary).should().promptUserToSubmitCredentials()
    }

    @Given("my login credentials have been saved")
    fun my_login_credentials_have_been_saved() {
        given(tokenStore.token).willReturn("validToken")
    }

    @Then("I should be shown the Accounts and Roundups report")
    fun i_should_be_shown_the_accounts_and_roundups_report() {
        then(showAccountsAndRoundupsOutputBoundary).should().showReport(any())
    }
}
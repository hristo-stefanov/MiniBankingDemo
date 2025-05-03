package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.business.interactors.shared.PresentAccountsAndRoundupsInteractor
import hristostefanov.minibankingdemo.business.interactors.shared.PresentAccountsAndRoundUpsOutputBoundary
import hristostefanov.minibankingdemo.business.interactors.startup.StartupInteractor
import hristostefanov.minibankingdemo.business.interactors.startup.StartupOutputBoundary
import hristostefanov.minibankingdemo.any
import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock
import java.time.ZoneId

class StartupSteps {
//    @Inject
//    @NavigationChannel
//    internal lateinit var navigationChannel: Channel<Navigation>

//    @Inject
//    internal lateinit var tokenStore: TokenStore

//

    private val tokenStore: TokenStore = mock()

    private val startupOutputBoundary: StartupOutputBoundary = mock()
    private val presentAccountsAndRoundupsOutputBoundary: PresentAccountsAndRoundUpsOutputBoundary =
        mock()
    private val repository: Repository = mock()

    private val presentAccountsAndRoundupsInteractor =
        PresentAccountsAndRoundupsInteractor(ZoneId.systemDefault(), repository, presentAccountsAndRoundupsOutputBoundary)
    private lateinit var startupInteractor: StartupInteractor


    @Before("@steps:startup")
    fun beforeEachScenario() = runTest {
//        TestApp.component.inject(this)
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        startupInteractor =
            StartupInteractor(startupOutputBoundary, tokenStore, presentAccountsAndRoundupsInteractor, testDispatcher)
    }

    @Given("my login credentials have not been saved")
    fun my_login_credentials_have_not_been_saved() {
        given(tokenStore.token).willReturn("")
    }

    @When("I launch the app")
    fun i_launch_the_app() = runTest {
        given(repository.findAllAccounts()).willReturn(emptyList())
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
    fun i_should_be_shown_the_accounts_and_roundups_report() = runTest {
        advanceUntilIdle()
        then(presentAccountsAndRoundupsOutputBoundary).should().present(any())
    }
}
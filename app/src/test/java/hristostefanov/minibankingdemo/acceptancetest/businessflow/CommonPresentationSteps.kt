package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.acceptancetest.technical.TestApp
import hristostefanov.minibankingdemo.presentation.Navigation
import hristostefanov.minibankingdemo.ui.AccountsFragmentDirections
import hristostefanov.minibankingdemo.util.NavigationChannel
import io.cucumber.java.Before
import io.cucumber.java.en.Then
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import javax.inject.Inject

class CommonPresentationSteps {
    @Inject
    @NavigationChannel
    lateinit var navigationChannel: Channel<Navigation>

    @Before("@steps:login or @steps:logout")
    fun beforeEachScenario() {
        TestApp.component.inject(this)
    }

    @Then("I should be asked to login")
    fun i_should_be_asked_to_login() {
        val nav = runBlocking {
            navigationChannel.receive()
        }

        Assertions.assertThat(nav).isEqualTo(Navigation.Forward(AccountsFragmentDirections.toAccessTokenDestination()))
    }

}
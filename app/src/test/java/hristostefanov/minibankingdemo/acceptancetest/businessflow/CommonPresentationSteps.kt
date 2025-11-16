package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.acceptancetest.technical.TestApp
import hristostefanov.minibankingdemo.presentation.MainCommand
import hristostefanov.minibankingdemo.ui.AccountsFragmentDirections
import hristostefanov.minibankingdemo.util.MainCommandChannel
import io.cucumber.java.Before
import io.cucumber.java.en.Then
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions
import javax.inject.Inject

class CommonPresentationSteps {
    @Inject
    @MainCommandChannel
    lateinit var mainCommandChannel: Channel<MainCommand>

    @Before("@steps:login or @steps:logout or @steps:autologin")
    fun beforeEachScenario() {
        TestApp.component.inject(this)
    }

    @Then("I should be asked to login")
    fun i_should_be_asked_to_login() = runTest {
        val nav = mainCommandChannel.receive()

        Assertions.assertThat(nav).isEqualTo(MainCommand.NavigateForward(AccountsFragmentDirections.toLoginDestination()))
    }

}
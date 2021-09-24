package hristostefanov.minibankingdemo.integrationtest.steps

import hristostefanov.minibankingdemo.integrationtest.TestApp
import hristostefanov.minibankingdemo.integrationtest.TestAutomation
import hristostefanov.minibankingdemo.presentation.AccountsViewModel
import io.cucumber.java8.En
import io.cucumber.java8.HookBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import javax.inject.Inject


class EncourageUsersToSaveMoneySteps : En {
    @Inject
    lateinit var automation: TestAutomation

    private lateinit var accountsViewModel: AccountsViewModel

    @ExperimentalCoroutinesApi
    private val testDispatcher = TestCoroutineDispatcher()

    init {
        TestApp.component.getSessionRegistry().sessionComponent.inject(this)

        Before(HookBody {
            Dispatchers.setMain(testDispatcher)
        })

        After(HookBody {
            Dispatchers.resetMain()
            testDispatcher.cleanupTestCoroutines()
        })

        Given("the calculated round-up for my account is 1.0") {
            automation.theCalculatedRoundUpIsOne()
        }
        When("I view this account") {
            accountsViewModel = automation.openAccountScreen()
            accountsViewModel.onAccountSelectionChanged(0)
        }
        Then("I should be offered to save {string}") { offer: String ->
//            assertThat(accountsViewModel.roundUpAmountText.value, `is`(offer))
        }
        And("I should be able to transfer the offered amount to a savings goal") {
//            assertThat(accountsViewModel.transferCommandEnabled.value, `is`(true))
        }
    }
}
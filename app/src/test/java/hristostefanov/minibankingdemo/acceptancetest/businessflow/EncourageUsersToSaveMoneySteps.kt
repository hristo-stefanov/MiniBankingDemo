package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.acceptancetest.technical.TestApp
import hristostefanov.minibankingdemo.presentation.AccountsViewModel
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import javax.inject.Inject


class EncourageUsersToSaveMoneySteps {
    @Inject
    lateinit var automation: PresentationTestAutomation

    private lateinit var accountsViewModel: AccountsViewModel

    init {
        TestApp.component.getSessionRegistry().sessionComponent.inject(this)
    }

    @Given("the calculated round-up for my account is {double}")
    fun the_calculated_round_up_for_my_account_is(amount: Double) {
        automation.login()
        automation.theCalculatedRoundUpIs(amount.toBigDecimal())
    }

    @When("I view this account")
    fun i_view_this_account() {
        accountsViewModel = automation.openAccountScreen()
        accountsViewModel.onAccountSelectionChanged(0)
    }

    @Then("I should be offered to save {string}")
    fun i_should_be_offered_to_save(offer: String) {
        assertThat(accountsViewModel.roundUpAmountText.value, `is`(offer))
    }

    @Then("I should be able to transfer the offered amount to a savings goal")
    fun i_should_be_able_to_transfer_the_offered_amount_to_a_savings_goal() {
        assertThat(accountsViewModel.transferCommandEnabled.value, `is`(true))
    }
}

package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.acceptancetest.technical.TestApp
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import java.math.BigDecimal
import javax.inject.Inject
import io.cucumber.java.Before

private const val ACCOUNT_NUM = "12345678"

class RoundUpSteps {
    // shared data between steps
    private lateinit var result: BigDecimal

    @Inject
    internal lateinit var automation: BusinessRulesTestAutomation

    @Before("@steps:roundUp")
    fun beforeEachScenario() {
        TestApp.component.inject(this)
    }

    @Given("the following transactions in an account")
    fun the_following_transactions_in_an_account(transactions: List<BigDecimal>) {
        automation.createAccount(ACCOUNT_NUM, "GBP", transactions)
    }

    @When("the round up amount is calculated")
    fun the_round_up_amount_is_calculated() {
        result = automation.calculateRoundUp(ACCOUNT_NUM)
    }

    @Then("the result will be {bigdecimal}")
    fun the_result_will_be(expected: BigDecimal) {
        assertThat(result, `is`(expected))
    }
}

package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.acceptancetest.technical.TestApp
import hristostefanov.minibankingdemo.business.calcRoundup
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import java.math.BigDecimal
import javax.inject.Inject
import io.cucumber.java.Before
import kotlinx.coroutines.test.runTest

private const val ACCOUNT_NUM = "12345678"

class RoundUpCalculationSteps {
    // shared data between steps
    private lateinit var result: BigDecimal

    @Inject
    internal lateinit var automation: BusinessRulesTestAutomation

    private lateinit var transactions: List<BigDecimal>

    @Before("@steps:roundUpCalculation")
    fun beforeEachScenario() {
        TestApp.component.inject(this)
    }

    @Given("the following eligible transactions, with these amounts:")
    fun the_following_transactions_are_eligible_for_round_up_calculation(transactions: List<BigDecimal>) {
//        automation.createAccount(ACCOUNT_NUM, "GBP", transactions)
        this.transactions = transactions

    }

    @When("the suggested round-up amount is calculated")
    fun the_suggested_round_up_amount_is_calculated() {
//        result = automation.calculateRoundUp(ACCOUNT_NUM)
        result = calcRoundup(transactions)
    }

    @Then("the result should be {bigdecimal}")
    fun the_result_will_be(expected: BigDecimal) {
        assertThat(result, `is`(expected))
    }
}

package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.acceptancetest.technical.TestApp
import hristostefanov.minibankingdemo.business.calcRoundup
import hristostefanov.minibankingdemo.business.entities.Source
import hristostefanov.minibankingdemo.business.entities.Status
import hristostefanov.minibankingdemo.business.entities.Transaction
import hristostefanov.minibankingdemo.business.isSpendingTransaction
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions.assertThat
import java.math.BigDecimal

private const val ACCOUNT_NUM = "12345678"

class CalculationsSteps {
    // shared data between steps
    private lateinit var result: BigDecimal

//    @Inject
//    internal lateinit var automation: BusinessRulesTestAutomation

    private lateinit var transactions: List<BigDecimal>

    private lateinit var tx: Transaction
    private var isEligible = false

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
        assertThat(result).isEqualTo(expected)
    }

    // ===

    // Note: the new syntax fails with Scenario outline and multiple paramater steps
    @Given("^I have a transaction from (.+) that is (.+) and (.+)$")
    fun i_have_a_transaction_from_external_with_settled_and_outbound(source: String, status: String, direction: String) {
        val amount = when (direction) {
            "outbound" -> -42.toBigDecimal()
            "inbound" -> 42.toBigDecimal()
            else -> throw IllegalArgumentException()
        }
        tx = Transaction(
            amount = amount,
            status = Status.valueOf(status.toUpperCase()),
            source = Source.valueOf(source.toUpperCase())
        )
    }

    @When("the transaction is evaluated")
    fun the_eligibility_for_roundup_suggestion_is_calculated() {
        isEligible = isSpendingTransaction(tx)
    }

    @Then("^the transaction should be classified as (.+)$")
    fun the_result_should_be_eligibility(expected: String) {
        val expectedIsEligible = when(expected) {
            "spending" -> true
            "non-spending" -> false
            else -> throw IllegalArgumentException()
        }
        assertThat(isEligible).isEqualTo(expectedIsEligible)
    }

}

package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.business.calcAccountRoundUp
import hristostefanov.minibankingdemo.business.entities.Source
import hristostefanov.minibankingdemo.business.entities.Status
import hristostefanov.minibankingdemo.business.entities.Transaction
import hristostefanov.minibankingdemo.business.isSpendingTransaction
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions.assertThat
import java.math.BigDecimal
import java.util.ArrayDeque
import java.util.Queue

private const val ACCOUNT_NUM = "12345678"

class CalculationsSteps {
    private lateinit var isEligibleQueue: Queue<Boolean>
    private lateinit var amountList: List<BigDecimal>
    private lateinit var result: BigDecimal

    private lateinit var transaction: Transaction
    private var isSpendingTransaction = false

    @Given("an account has transactions with the following amounts and eligibility for round up:")
    fun an_account_has_transactions_with_the_following_amounts_and_eligibility_for_round_up(
        amountsAndEligibilities: List<Map<String, String>>
    ) {
        // TODO consider using Mockk for stubbing the top level function isEligible()
        // as Mockito cannot do that.
        val isEligibleList = amountsAndEligibilities.map { it ->
            when (it["eligibility"]) {
                "eligible" -> true
                "ineligible" -> false
                else -> throw IllegalArgumentException()
            }
        }

        isEligibleQueue = ArrayDeque(isEligibleList)
        amountList = amountsAndEligibilities.map { BigDecimal(it["amount"]) }
    }

    @When("the round-up amount for the account is calculated")
    fun the_round_up_amout_for_the_account_is_calculated() {
        val isEligible = { _: Transaction ->
            isEligibleQueue.remove()
        }

        // Only the amount matters as isEligible() is stubbed.
        val transactions =
            amountList.map { amount -> Transaction(amount, Status.SETTLED, Source.EXTERNAL) }

        result = calcAccountRoundUp(transactions, isEligible)
    }

    @Then("the result should be {bigdecimal}")
    fun the_result_will_be(expected: BigDecimal) {
        assertThat(result).isEqualTo(expected)
    }

    // Note: Using Cucumber expressions fails with Scenario outline and multiple paramater steps,
    // hence using the old regex syntax.
    @Given("^I have a transaction from (.+) that is (.+) and (.+)$")
    fun i_have_a_transaction_from_external_with_settled_and_outbound(
        source: String,
        status: String,
        direction: String
    ) {
        val amount = when (direction) {
            "outbound" -> -42.toBigDecimal()
            "inbound" -> 42.toBigDecimal()
            else -> throw IllegalArgumentException()
        }
        transaction = Transaction(
            amount = amount,
            status = Status.valueOf(status.toUpperCase()),
            source = Source.valueOf(source.toUpperCase())
        )
    }

    @When("it is evaluated")
    fun it_is_evaluated() {
        isSpendingTransaction = isSpendingTransaction(transaction)
    }

    @Then("^it should be classified as (.+)$")
    fun it_should_be_classified_as(expected: String) {
        val expectedIsSpendingTransaction = when (expected) {
            "spending" -> true
            "non-spending" -> false
            else -> throw IllegalArgumentException()
        }
        assertThat(isSpendingTransaction).isEqualTo(expectedIsSpendingTransaction)
    }

}

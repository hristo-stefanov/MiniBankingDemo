package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.acceptancetest.technical.TestApp
import hristostefanov.minibankingdemo.business.calcAccountRoundUp
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
import java.util.ArrayDeque
import java.util.Deque
import java.util.Queue

private const val ACCOUNT_NUM = "12345678"

class CalculationsSteps {
    // shared data between steps
    private lateinit var result: BigDecimal

//    @Inject
//    internal lateinit var automation: BusinessRulesTestAutomation

    private lateinit var transactions: List<BigDecimal>

    private lateinit var tx: Transaction
    private var isEligible = false

    private lateinit var isEligibleQueue: Queue<Boolean>
    private lateinit var amountList: List<BigDecimal>

//    @Before("@steps:roundUpCalculation")
//    fun beforeEachScenario() {
//        TestApp.component.inject(this)
//    }

    @Given("an account has transactions with the following amounts and eligibility for round up:")
    fun an_account_has_transactions_with_the_following_amounts_and_eligibility_for_round_up(amountsAndEligibilities: List<Map<String, String>>) {

        val isEligibleList =  amountsAndEligibilities.map { it ->
            when (it["eligibility"]) {
                "eligible" -> true
                "ineligible" -> false
                else -> throw IllegalArgumentException()
            }
        }

        isEligibleQueue = ArrayDeque(isEligibleList)
        amountList = amountsAndEligibilities.map { BigDecimal(it["amount"]) }

//        automation.createAccount(ACCOUNT_NUM, "GBP", transactions)
//        this.transactions = transactions
    }

    @When("the round-up amount for the account is calculated")
    fun the_round_up_amout_for_the_account_is_calculated() {
        val isEligible = { _: Transaction ->
            isEligibleQueue.remove()
        }

        val transactions = amountList.map { Transaction(it, Status.SETTLED, Source.EXTERNAL)  }
//        result = automation.calculateRoundUp(ACCOUNT_NUM)
        result = calcAccountRoundUp(transactions, isEligible)
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

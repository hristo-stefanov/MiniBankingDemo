package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.any
import hristostefanov.minibankingdemo.business.calcAccountRoundUp
import hristostefanov.minibankingdemo.business.calcStartOfSevenDayWindowIncludingToday
import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.entities.Source
import hristostefanov.minibankingdemo.business.entities.Status
import hristostefanov.minibankingdemo.business.entities.Transaction
import hristostefanov.minibankingdemo.business.isSpendingTransaction
import io.cucumber.java.ParameterType
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.ArrayDeque
import java.util.Queue

private const val ACCOUNT_NUM = "12345678"

class CalculationsSteps {
    private lateinit var isSpendingTransactionFlagQueue: Queue<Boolean>
    private lateinit var amountList: List<BigDecimal>
    private lateinit var accountRoundup: BigDecimal

    private lateinit var transaction: Transaction
    private var isSpendingTransaction = false

    private lateinit var now: OffsetDateTime
    private lateinit var since: OffsetDateTime

    @ParameterType(value = ".*", name = "offsetDateTime")
    fun offsetDateTime(value: String) = OffsetDateTime.parse(value)

    @Given("the current local date and time is {offsetDateTime}")
    fun the_current_local_date_and_time_is(now: OffsetDateTime) {
        this.now = now
    }

    @When("account transactions are requested")
    fun account_transactions_are_requested() {
        since = calcStartOfSevenDayWindowIncludingToday(now)
    }

    @Then("the ones {offsetDateTime} date and time should be requested")
    fun the_ones_date_and_time_should_be_requested(expectedSince: OffsetDateTime) {
        assertThat(since).isEqualTo(expectedSince)
    }

    @Given("an account has transactions for a period with the following amounts and is spending flags:")
    fun an_account_has_transactions_for_a_period_with_the_following_amounts_and_is_spending_flags(
        amountsAndIsSpendingFlags: List<Map<String, String>>
    ) {
        // TODO consider using Mockk for stubbing the policy
        // as Mockito cannot do that.
        val isSpendingTransactionFlagList = amountsAndIsSpendingFlags.map { it ->
            when (it["is spending"]) {
                "yes" -> true
                "no" -> false
                else -> throw IllegalArgumentException()
            }
        }

        isSpendingTransactionFlagQueue = ArrayDeque(isSpendingTransactionFlagList)
        amountList = amountsAndIsSpendingFlags.map { BigDecimal(it["amount"]) }
    }

    @When("the account round-up amount for the period is calculated")
    fun the_round_up_amout_for_the_period_is_calculated() = runTest {
        val isSpendingTransactionPolicy = { _: Transaction ->
            isSpendingTransactionFlagQueue.remove()
        }

        // Only the amount matters as the policy is stubbed.
        val transactions =
            amountList.map { amount -> Transaction(amount, Status.SETTLED, Source.EXTERNAL) }

        val repository: Repository = mock()
        given(repository.findTransactions(any(), any())).willReturn(transactions)


        accountRoundup = calcAccountRoundUp(repository, "1", OffsetDateTime.now(), isSpendingTransactionPolicy)
    }

    @Then("the result should be {bigdecimal}")
    fun the_result_will_be(expected: BigDecimal) {
        assertThat(accountRoundup).isEqualTo(expected)
    }

    // Note: Using Cucumber expressions fails with Scenario outline and steps with
    // multiple paramaters, hence using the old regex syntax.
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

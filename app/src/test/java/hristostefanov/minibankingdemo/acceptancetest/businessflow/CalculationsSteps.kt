package hristostefanov.minibankingdemo.acceptancetest.businessflow

import com.google.common.collect.Multimaps.index
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
import kotlin.String

private const val ACCOUNT_NUM = "12345678"

class CalculationsSteps {
    private lateinit var isSpendingTransactionFlagMap: Map<String, Boolean>
    private lateinit var isDatedWithinAWeekFlagMap: Map<String, Boolean>
    private lateinit var transactionRoundUpMap: Map<String, BigDecimal>
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

    @Given("an account with these transactions:")
    fun an_account_with_these_transactions(transactionsTable: List<Map<String, String>>) {
        // TODO consider using Mockk for stubbing the policy
        // as Mockito cannot do that.

        val isSpendingTransactionFlagList = transactionsTable.mapIndexed { index, map ->
            val isSpending = when (map["is spending"]) {
                "yes" -> true
                "no" -> false
                else -> throw IllegalArgumentException()
            }
            index.toString() to isSpending
        }
        isSpendingTransactionFlagMap = isSpendingTransactionFlagList.toMap()

        val isDatedWithinAWeekFlagList = transactionsTable.mapIndexed { index, it ->
            val isDatedWithinAWeek = when (it["is dated within a week"]) {
                "yes" -> true
                "no" -> false
                else -> throw IllegalArgumentException()
            }
            index.toString() to isDatedWithinAWeek
        }
        isDatedWithinAWeekFlagMap = isDatedWithinAWeekFlagList.toMap()

        val transactionRoundPairList = transactionsTable.mapIndexed { index, it -> index.toString() to BigDecimal(it["round-up"]) }
        transactionRoundUpMap = transactionRoundPairList.toMap()
    }

    @When("the account round-up is calculated")
    fun the_account_round_up_is_calculated() = runTest {
        val isSpendingTransactionPolicy = { tx: Transaction ->
            isSpendingTransactionFlagMap[tx.id]!!
        }

        val calcTransactionRoundUpPolicy = { it: Transaction ->
            transactionRoundUpMap[it.id]!!
        }

        val repository: Repository = mock()
        given(repository.findTransactions(any(), any())).willAnswer { invocation ->
            val accountId = invocation.arguments[0] as String
            val since = invocation.arguments[1] as OffsetDateTime

            // Only the transaction Id matter since the calculation policies are stubbed
            transactionRoundUpMap.entries
                .filter { isDatedWithinAWeekFlagMap[it.key]!! }
                .map{ it -> Transaction(
                amount = BigDecimal.ZERO,
                status = Status.SETTLED,
                source = Source.EXTERNAL,
                id = it.key
            )  }
        }


        accountRoundup = calcAccountRoundUp(
            repository = repository,
            accountId = "1",
            since = OffsetDateTime.now(),
            calcTransactionRoundUpPolicy = calcTransactionRoundUpPolicy,
            isSpendingTransactionPolicy = isSpendingTransactionPolicy
        )
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

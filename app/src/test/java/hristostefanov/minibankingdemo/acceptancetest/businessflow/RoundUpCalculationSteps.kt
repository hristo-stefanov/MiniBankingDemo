package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.business.calcStartOfSevenDayWindowIncludingToday
import hristostefanov.minibankingdemo.business.calcTransactionRoundUp
import hristostefanov.minibankingdemo.business.entities.Account
import hristostefanov.minibankingdemo.business.entities.Source
import hristostefanov.minibankingdemo.business.entities.Status
import hristostefanov.minibankingdemo.business.entities.Transaction
import hristostefanov.minibankingdemo.business.isSpendingTransaction
import hristostefanov.minibankingdemo.usecase.output.AccountsAndRoundUpsSummary
import hristostefanov.minibankingdemo.usecase.summarize
import io.cucumber.java.DataTableType
import io.cucumber.java.ParameterType
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.Currency

private const val ACCOUNT_NUM = "12345678"

class RoundUpCalculationSteps {
    private lateinit var isSpendingTransactionFlagMap: Map<String, Boolean>
    private lateinit var transactionRoundUpMap: Map<String, BigDecimal>
    private lateinit var accounts: List<Account>
    private lateinit var summary: AccountsAndRoundUpsSummary
    private lateinit var dataset: Map<Account, List<Transaction>>
    private lateinit var result: BigDecimal
    private lateinit var transaction: Transaction
    private var isSpendingTransaction = false
    private lateinit var now: OffsetDateTime
    private lateinit var since: OffsetDateTime

    @ParameterType(value = ".*", name = "offsetDateTime")
    fun offsetDateTime(value: String) = OffsetDateTime.parse(value)

    @DataTableType
    fun accountTransformer(entry: Map<String, String>): Account {
        return Account(
            id = entry["account number"]!!,
            accountNum = entry["account number"]!!,
            categoryUid = "category",
            currency = Currency.getInstance(entry["currency"]),
            balance = BigDecimal(entry["balance"]!!)
        )
    }

    @Given("a transaction with amount of {bigdecimal}")
    fun a_transaction_with_amout_of(amount: BigDecimal) {
        // Only the amount matters
        transaction =
            Transaction(amount = amount, status = Status.SETTLED, source = Source.EXTERNAL)
    }

    @When("the transaction round-up is calculated")
    fun the_transaction_round_up_is_calculated() {
        result = calcTransactionRoundUp(transaction)
    }

    @Given("the current local date and time is {offsetDateTime}")
    fun the_current_local_date_and_time_is(now: OffsetDateTime) {
        this.now = now
    }

    @When("the week-long period is evaluated")
    fun the_week_long_period_is_evaluated() {
        since = calcStartOfSevenDayWindowIncludingToday(now)
    }

    @Then("the start of the period should be {offsetDateTime} date and time")
    fun the_start_of_the_period_should_be(expectedSince: OffsetDateTime) {

        assertThat(since).isEqualTo(expectedSince)
    }

    @Given("I have the following accounts for a week-long period")
    fun i_have_the_following_accounts_for_a_week_long_period(accounts: List<Account>) = runTest {
        this@RoundUpCalculationSteps.accounts = accounts
    }

    @Given("I have these transactions")
    fun i_have_these_transactions(transactionsTable: List<Map<String, String>>) = runTest {
        // We use the index as transaction id

        val isSpendingTransactionFlagList = transactionsTable.mapIndexed { index, map ->
            val isSpending = when (map["is spending"]) {
                "yes" -> true
                "no" -> false
                else -> throw IllegalArgumentException()
            }
            index.toString() to isSpending
        }
        isSpendingTransactionFlagMap = isSpendingTransactionFlagList.toMap()

        val transactionRoundPairList =
            transactionsTable.mapIndexed { index, it -> index.toString() to BigDecimal(it["round-up"]) }
        transactionRoundUpMap = transactionRoundPairList.toMap()

        dataset= transactionsTable
            .withIndex()
            .groupBy { it.value["account number"]!! }
            .entries.map { entry ->
                val transactions = entry.value.map { indexedValue ->
                    Transaction(
                        amount = BigDecimal.ZERO, // Not used becuase the round-up amount is stubbed
                        status = Status.SETTLED, // Not used because "is spending" rule is stubbed
                        source = Source.EXTERNAL, // Not used because "is spending" rule is stubbed
                        title = indexedValue.index.toString(),
                        id = indexedValue.index.toString()
                    )
                }
                val account = accounts.find { it.id == entry.key }!!
                account to transactions
            }.toMap()
    }

    @When("I'm presented with Accounts and Round-ups summary")
    fun i_m_presented_with_accounts_and_roundups_summary() = runTest {
        val isSpendingTransactionPolicy = { tx: Transaction ->
            isSpendingTransactionFlagMap[tx.id]!!
        }

        val calcTransactionRoundUpPolicy = { it: Transaction ->
            transactionRoundUpMap[it.id]!!
        }

        summary = summarize(dataset, calcTransactionRoundUpPolicy, isSpendingTransactionPolicy)
    }

    @Then("the following information should be included")
    fun the_following_information_should_be_included(dataTable: List<Map<String, String>>) {
        val expectedSummary = AccountsAndRoundUpsSummary(
            dataTable.map {
                AccountsAndRoundUpsSummary.Item(
                    accountId = it["account number"]!!,
                    number = it["account number"]!!,
                    roundUp = BigDecimal(it["round-up"]),
                    balance = BigDecimal(it["balance"]),
                    currency = Currency.getInstance(it["currency"])
                )
            }
        )

        assertThat(summary).isEqualTo(expectedSummary)
    }

    @Then("the result should be {bigdecimal}")
    fun the_result_will_be(expected: BigDecimal) {
        assertThat(result).isEqualTo(expected)
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

package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.any
import hristostefanov.minibankingdemo.business.calcTransactionRoundUp
import hristostefanov.minibankingdemo.business.calcStartOfSevenDayWindowIncludingToday
import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.entities.Account
import hristostefanov.minibankingdemo.business.entities.Source
import hristostefanov.minibankingdemo.business.entities.Status
import hristostefanov.minibankingdemo.business.entities.Transaction
import hristostefanov.minibankingdemo.business.isSpendingTransaction
import hristostefanov.minibankingdemo.usecase.AccountsAndRoundUpsModel
import hristostefanov.minibankingdemo.usecase.PresentAccountsAndRoundUpsOutputBoundary
import hristostefanov.minibankingdemo.usecase.PresentAccountsAndRoundupsInteractor
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
import java.util.Currency

private const val ACCOUNT_NUM = "12345678"

class RoundUpCalculationSteps {
    private lateinit var isSpendingTransactionFlagMap: Map<String, Boolean>
    private lateinit var transactionIdToDateMap: Map<String, OffsetDateTime>
    private lateinit var transactionRoundUpMap: Map<String, BigDecimal>
    private lateinit var result: BigDecimal

    private lateinit var transaction: Transaction
    private var isSpendingTransaction = false

    private lateinit var now: OffsetDateTime
    private lateinit var accountId: String
    private lateinit var since: OffsetDateTime

    @ParameterType(value = ".*", name = "offsetDateTime")
    fun offsetDateTime(value: String) = OffsetDateTime.parse(value)

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

    @Given("an account with these transactions:")
    fun an_account_with_these_transactions(transactionsTable: List<Map<String, String>>) {
        // TODO consider using Mockk for stubbing the policy
        // as Mockito cannot do that.

        accountId = "1"

        val isSpendingTransactionFlagList = transactionsTable.mapIndexed { index, map ->
            val isSpending = when (map["is spending"]) {
                "yes" -> true
                "no" -> false
                else -> throw IllegalArgumentException()
            }
            index.toString() to isSpending
        }
        isSpendingTransactionFlagMap = isSpendingTransactionFlagList.toMap()

        now = OffsetDateTime.parse("2020-05-03T00:00Z")
        val transactionIdToDateList = transactionsTable.mapIndexed { index, it ->
            val date = when (it["is dated within a week"]) {
                "yes" -> OffsetDateTime.parse("2020-05-01T00:00Z")
                "no" -> OffsetDateTime.parse("2020-04-03T00:00Z")
                else -> throw IllegalArgumentException()
            }
            index.toString() to date
        }
        transactionIdToDateMap = transactionIdToDateList.toMap()

        val transactionRoundPairList =
            transactionsTable.mapIndexed { index, it -> index.toString() to BigDecimal(it["round-up"]) }
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
        given(repository.findAllAccounts()).willReturn(listOf(
            Account(accountId, accountId, "", Currency.getInstance("GBP"),
            BigDecimal.ZERO ))
        )

        given(repository.findTransactions(any(), any())).willAnswer { invocation ->
            val accountIdArg = invocation.arguments[0] as String
            val sinceArg = invocation.arguments[1] as OffsetDateTime

            transactionRoundUpMap.entries
                .filter {
                    val txDate = transactionIdToDateMap[it.key]!!
                    accountId == accountIdArg && txDate > sinceArg
                }
                .map { it ->
                    // Only the transaction Id matter since the calculation policies are stubbed
                    Transaction(
                        amount = BigDecimal.ZERO,
                        status = Status.SETTLED,
                        source = Source.EXTERNAL,
                        id = it.key
                    )
                }
        }

        val output = object: PresentAccountsAndRoundUpsOutputBoundary {
            override fun present(model: AccountsAndRoundUpsModel) {
                result = model.items.find { it.accountId == accountId }!!.roundUp
            }
        }

        val interactor = PresentAccountsAndRoundupsInteractor(
            now = now,
            repository = repository,
            isSpendingTransactionPolicy = isSpendingTransactionPolicy,
            calcTransactionRoundUpPolicy = calcTransactionRoundUpPolicy,
            output = output,
        )

        interactor()
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

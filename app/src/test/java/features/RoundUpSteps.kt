package features

import hristostefanov.minibankingdemo.business.interactors.CalcRoundUpInteractor
import hristostefanov.minibankingdemo.cucumber.MockService2
import hristostefanov.minibankingdemo.cucumber.TestComponentRegistry
import hristostefanov.minibankingdemo.data.models.AccountV2
import hristostefanov.minibankingdemo.data.models.CurrencyAndAmount
import hristostefanov.minibankingdemo.data.models.FeedItem
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.absoluteValue
import kotlin.math.roundToLong


class RoundUpSteps : En {
    private lateinit var result: BigDecimal

    @Inject
    lateinit var service: MockService2

    @Inject
    lateinit var interactor: CalcRoundUpInteractor

    init {
        TestComponentRegistry.applicationComponent.getSessionRegistry().sessionComponent.inject(this)

        Given("the following transactions in my {string}") { accountId: String, dataTable: DataTable ->
            val list: MutableList<Double> = dataTable.asList(Double::class.java)
            val feedItems = list.map {
                FeedItem(
                    direction = if (it >= 0) "IN" else "OUT",
                    amount = CurrencyAndAmount("GBP", (it * 100.0).roundToLong().absoluteValue),
                    status = "SETTLED"
                )
            }

            service.accounts =
                listOf(AccountV2(accountUid = accountId, defaultCategory = "", currency = "GBP"))
            service.feedItemsToAccountId = mapOf(accountId to feedItems)
        }

        When("I access {string}") { accountId: String ->
            runBlocking {
                result = interactor.execute(accountId, LocalDate.now())
            }
        }

        Then("I will be asked to save {double}") { double: Double ->
            assertThat(result.toDouble(), Matchers.`is`(double))
        }

    }
}
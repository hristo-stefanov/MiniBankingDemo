package features

import hristostefanov.minibankingdemo.business.interactors.CalcRoundUpInteractor
import hristostefanov.minibankingdemo.integrationtest.ServiceStub
import hristostefanov.minibankingdemo.integrationtest.TestApp
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
    lateinit var service: ServiceStub

    @Inject
    lateinit var interactor: CalcRoundUpInteractor

    init {
        TestApp.component.getSessionRegistry().sessionComponent.inject(this)

        Given("I have an account {string} in {string}") { accountId: String, currency: String ->
            service.accounts = listOf(
                AccountV2(
                    accountUid = accountId, defaultCategory = "", currency = currency
                )
            )
        }

        And("the following transactions in my account {string}") { accountId: String, dataTable: DataTable ->
            val list: MutableList<Double> = dataTable.asList(Double::class.java)
            val feedItems = list.map {
                FeedItem(
                    direction = if (it >= 0) "IN" else "OUT",
                    amount = CurrencyAndAmount("GBP", (it * 100.0).roundToLong().absoluteValue),
                    status = "SETTLED"
                )
            }

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
package hristostefanov.minibankingdemo.integrationtest.steps

import hristostefanov.minibankingdemo.business.interactors.CalcRoundUpInteractor
import hristostefanov.minibankingdemo.integrationtest.TestApp
import hristostefanov.minibankingdemo.integrationtest.TestAutomation
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import java.math.BigDecimal
import javax.inject.Inject

private const val ACCOUNT_NUM = "12345678"

class RoundUpSteps : En {
    private lateinit var result: BigDecimal

    @Inject
    lateinit var automation: TestAutomation

    @Inject
    lateinit var interactor: CalcRoundUpInteractor

    init {
        TestApp.component.getSessionRegistry().sessionComponent.inject(this)

        Given("the following transactions in an account") { dataTable: DataTable ->
            val list: MutableList<BigDecimal> = dataTable.asList(BigDecimal::class.java)

            automation.createAccount(ACCOUNT_NUM, "GBP", list)
        }

        When("the round up amount is calculated") {
            result = automation.calculateRoundUp(ACCOUNT_NUM)
        }

        Then("the result will be {bigdecimal}") { expected: BigDecimal ->
            assertThat(result, Matchers.`is`(expected))
        }
    }
}
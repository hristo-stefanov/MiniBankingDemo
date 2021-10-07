package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.acceptancetest.technical.BusinessRulesTestAutomationImpl
import hristostefanov.minibankingdemo.business.interactors.CalcRoundUpInteractor
import hristostefanov.minibankingdemo.acceptancetest.technical.TestApp
import io.cucumber.datatable.DataTable
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import java.math.BigDecimal
import javax.inject.Inject


private const val ACCOUNT_NUM = "12345678"

class RoundUpSteps {
    private lateinit var result: BigDecimal

    @Inject
    lateinit var automation: BusinessRulesTestAutomation

    @Inject
    lateinit var interactor: CalcRoundUpInteractor

    init {
        TestApp.component.getSessionRegistry().sessionComponent.inject(this)
    }

    @Given("the following transactions in an account")
    fun the_following_transactions_in_an_account(dataTable: DataTable) {
        val list: MutableList<BigDecimal> = dataTable.asList(BigDecimal::class.java)

        automation.createAccount(ACCOUNT_NUM, "GBP", list)
    }

    @When("the round up amount is calculated")
    fun the_round_up_amount_is_calculated() {
        result = automation.calculateRoundUp(ACCOUNT_NUM)
    }

    @Then("the result will be {bigdecimal}")
    fun the_result_will_be(expected: BigDecimal) {
        assertThat(result, `is`(expected))
    }
}

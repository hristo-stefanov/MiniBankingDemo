package features

import hristostefanov.minibankingdemo.business.interactors.CalcRoundUpInteractor
import hristostefanov.minibankingdemo.cucumber.MockService2
import hristostefanov.minibankingdemo.cucumber.TestComponentRegistry
import hristostefanov.minibankingdemo.data.models.AccountV2
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject


class RoundUpSteps : En {
    private lateinit var result: BigDecimal

    @Inject
    lateinit var service: MockService2

    @Inject
    lateinit var interactor: CalcRoundUpInteractor

    init {
        TestComponentRegistry.applicationComponent.getSessionRegistry().sessionComponent.inject(this)

        Given("the following transactions in my {string}") { accountId: String, dataTable: DataTable ->
            service.accounts = listOf(AccountV2(accountId))
        }

        When("I access {string}") { accountId: String ->
            runBlockingTest {
                result = interactor.execute(accountId, LocalDate.now())
            }
        }

        Then("I will be asked to save {double}") { double: Double ->
            assertThat(result.toDouble(), Matchers.`is`(double))
        }

    }
}
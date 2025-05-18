package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.entities.Account
import hristostefanov.minibankingdemo.business.interactors.shared.AccountsAndRoundUpsModel
import hristostefanov.minibankingdemo.business.interactors.shared.PresentAccountsAndRoundUpsOutputBoundary
import hristostefanov.minibankingdemo.business.interactors.shared.PresentAccountsAndRoundupsInteractor
import io.cucumber.java.DataTableType
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.test.runTest
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.Currency


class PresentAccountsAndRoundUpsSteps {
    @DataTableType
    fun accountTransformer(entry: Map<String, String>): Account {
        return Account(
            id = entry["number"]!!,
            accountNum = entry["number"]!!,
            categoryUid = "category",
            currency = Currency.getInstance(entry["currency"]),
            balance = BigDecimal(entry["balance"]!!)
        )
    }

    private lateinit var presentAccountsAndRoundupsInteractor: PresentAccountsAndRoundupsInteractor
    private lateinit var accounts: List<Account>
    private val output: PresentAccountsAndRoundUpsOutputBoundary = mock()
    private val repository: Repository = mock()

    @Given("I have the following accounts")
    fun i_have_the_following_accounts(accounts: List<Account>) = runTest {
        this@PresentAccountsAndRoundUpsSteps.accounts = accounts

        given(repository.findAllAccounts()).willReturn(accounts)
    }

    @And("the calculated round-up for each is")
    fun the_calculated_round_up_for_each_is(calculatedRoundUps: List<Map<String, String>>) {

        val calcAccountRoundUpStub: suspend (Repository, String, OffsetDateTime) -> BigDecimal =
            { repository: Repository, accountId: String, since: OffsetDateTime ->
                calculatedRoundUps.find { it["number"] == accountId }!!.let { BigDecimal(it["round-up"]) }
            }

        presentAccountsAndRoundupsInteractor = PresentAccountsAndRoundupsInteractor(
            repository = repository,
            output = this@PresentAccountsAndRoundUpsSteps.output,
            calcAccountRoundUpInteractorArg = calcAccountRoundUpStub,
            now = OffsetDateTime.parse("2025-05-18T00:00Z")
        )

    }

    @When("I'm presented with Accounts and Round-ups")
    fun i_m_presented_with_accounts_and_roundups() = runTest {
        presentAccountsAndRoundupsInteractor.execute()
    }

    @Then("the following information should be included")
    fun the_following_information_should_be_included(dataTable: List<Map<String, String>>) {
        val expectedModel = AccountsAndRoundUpsModel(
            dataTable.map {
                AccountsAndRoundUpsModel.Item(
                    accountId = it["number"]!!,
                    number = it["number"]!!,
                    roundUp = BigDecimal(it["round-up"]),
                    balance = BigDecimal(it["balance"]),
                    currency = Currency.getInstance(it["currency"])
                )
            }
        )
        then(output).should().present(expectedModel)
    }

}
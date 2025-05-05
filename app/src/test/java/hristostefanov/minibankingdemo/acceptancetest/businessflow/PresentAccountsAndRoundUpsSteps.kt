package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.business.entities.Account
import hristostefanov.minibankingdemo.business.interactors.shared.PresentAccountsAndRoundupsInteractor
import io.cucumber.datatable.DataTable
import io.cucumber.java.DataTableType
import io.cucumber.java.PendingException
import io.cucumber.java.en.Given
import io.cucumber.java.en.When
import io.cucumber.java.en.Then
import io.cucumber.java.it.Ma
import java.math.BigDecimal
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

    private lateinit var presetAccountsAndRoundupsInteractor: PresentAccountsAndRoundupsInteractor
    private lateinit var accounts: List<Account>

    @Given("I have the following accounts")
    fun i_have_the_following_accounts(accounts: List<Account>) {
        this.accounts = accounts
    }

    @Given("the suggested round-up for each is")
    fun the_suggested_round_up_for_each_is(suggestedRoundUps: List<Map<String, String>>) {


    }

    @When("I'm presented with Accounts and Round-ups")
    fun i_m_presented_with_accounts_and_roundups() {
        // Write code here that turns the phrase above into concrete actions
        throw PendingException()
    }

    @Then("the following information should be included")
    fun the_following_information_should_be_included(dataTable: DataTable?) {
        // Write code here that turns the phrase above into concrete actions
        // For automatic transformation, change DataTable to one of
        // E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or
        // Map<K, List<V>>. E,K,V must be a String, Integer, Float,
        // Double, Byte, Short, Long, BigInteger or BigDecimal.
        //
        // For other transformations you can register a DataTableType.
        throw PendingException()
    }

}
package hristostefanov.minibankingdemo.cucumber;

import java.util.Arrays;

import javax.inject.Inject;

import hristostefanov.minibankingdemo.business.interactors.CalcRoundUpInteractor;
import hristostefanov.minibankingdemo.data.models.AccountV2;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class StepDefinitions {

    public StepDefinitions() {
        TestComponentRegistry.applicationComponent.getSessionRegistry().getSessionComponent().inject(this);
    }

    @Inject
    MockService2 service;

    @Inject
    CalcRoundUpInteractor interactor;

    @Given("the following transactions in my {string}")
    public void theFollowingTransactionsInMy(String accountId, DataTable dataTable) {
        AccountV2 account = new AccountV2(accountId, null, null, null);
        service.setAccounts(Arrays.asList(account));
    }

    @When("I access {string}")
    public void iAccess(String accountId) {
    }

    @Then("I will be asked to save {double}")
    public void iWillBeAskedToSave(Double arg0) {
    }
}

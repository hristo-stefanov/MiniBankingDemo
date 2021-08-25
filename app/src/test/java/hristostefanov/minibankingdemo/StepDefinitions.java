package hristostefanov.minibankingdemo;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class StepDefinitions {

    @Given("the following transactions in my {string}")
    public void theFollowingTransactionsInMy(String arg0, DataTable dataTable) {

    }

    @When("I access {string}")
    public void iAccess(String arg0) {

    }

    @Then("I will be asked to save {double}")
    public void iWillBeAskedToSave(Double arg0) {
    }
}

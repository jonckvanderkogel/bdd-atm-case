package com.ing.bdd.integration;

import com.ing.bdd.model.Bill;
import com.ing.bdd.model.BillSet;
import com.ing.bdd.model.SimpleEither;
import com.ing.bdd.model.WithdrawBillsInput;
import com.ing.bdd.service.ATMService;
import com.ing.bdd.service.FeeCalculator;
import com.ing.bdd.service.FundsStorage;
import com.ing.bdd.testutil.FaultyAtmFun;
import graphql.GraphQLError;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.ing.bdd.testutil.Util.generateBillMap;
import static org.assertj.core.api.Assertions.assertThat;

public class StepDefinitions {
    private ATMService atmService;
    private String accountNr;
    private List<BillSet> billSets;
    private GraphQLError error;
    private BiFunction<Integer, Integer, Integer> randomAtmFun = (i, j) -> 1;


    @Given("The atm is faulty")
    public  void theAtmIsFaulty() {
        randomAtmFun = new FaultyAtmFun();
    }

    @Given("I have {int} Euros in my account")
    public void iHaveEurosInMyAccount(final int amount) {
        Map<Bill, Integer> alwaysEnoughCash = generateBillMap(amount, amount, amount, amount);
        FundsStorage fundsStorage = new FundsStorage((i, j) -> amount, alwaysEnoughCash);
        FeeCalculator feeCalculator = new FeeCalculator(fundsStorage, randomAtmFun);
        atmService = new ATMService(fundsStorage, feeCalculator);
    }

    @And("My account number is {word}")
    public void myAccountNrIs(String accountNr) {
        this.accountNr = accountNr;
    }

    @When("I withdraw {int} Euros")
    public void iWithdrawEuros(final int amount) {
        this.billSets = atmService.withdrawBills(new WithdrawBillsInput(amount, accountNr));
    }

    @When("I withdraw {int} Euros with fees")
    public void iWithdrawEurosWithFees(final int amount) {
        SimpleEither<GraphQLError, List<BillSet>> either = atmService.withdrawBillsWithFees(new WithdrawBillsInput(amount, accountNr));
        this.billSets = either.isSuccess() ? either.getSuccess() : Collections.emptyList();
        this.error = either.isError() ? either.getError() : null;
    }

    @Then("I expect the following set of bills")
    public void thenIExpectTheseBillSets(DataTable dataTable) {
        List<List<String>> rows = dataTable.asLists(String.class);
        List<BillSet> expectedBillSets = rows.stream()
            .map(l -> new BillSet(Bill.valueOf(l.get(1)), Integer.valueOf(l.get(0))))
            .collect(Collectors.toList());

        assertThat(expectedBillSets).hasSameElementsAs(this.billSets);
    }

    @Then("I expect the atm crashed")
    public void thenIExpectTheAtmCrashed() {
        assertThat(error.getMessage()).isEqualTo("ATM crashed!");
    }
}

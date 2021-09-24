package com.ing.bdd.service;

import com.ing.bdd.model.Balance;
import com.ing.bdd.model.Bill;
import com.ing.bdd.model.BillSet;
import com.ing.bdd.model.WithdrawBillsInput;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static com.ing.bdd.testutil.Util.generateBillMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ATMServiceTest {
    // Plenty of cash available in the machine
    private final Map<Bill, Integer> lotsOfCash = generateBillMap(1000, 1000, 1000, 1000);
    // only 5 10s available
    private final Map<Bill, Integer> onlyFewTens = generateBillMap(5, 0, 0, 0);
    private final BiFunction<Integer,Integer,Integer> randomFun = (i,j) -> 1000;

    @Test
    public void retrieveCurrentBalanceTest() {
        FundsStorage fundsStorage = new FundsStorage(randomFun, lotsOfCash);
        ATMService atmService = new ATMService(fundsStorage);

        Balance currentBalance = atmService.retrieveBalance("123");

        assertEquals(1000, currentBalance.getAmount());
    }

    @Test
    public void withdrawBillsWithPlentyOfCashAvailableTest() {
        FundsStorage fundsStorage = new FundsStorage(randomFun, lotsOfCash);
        ATMService atmService = new ATMService(fundsStorage);

        List<BillSet> billSets = atmService.withdrawBills(new WithdrawBillsInput(100, "123"));

        // just expecting 1 100 bill
        assertEquals(1, billSets.size());
        assertEquals(1, billSets.get(0).getNr());
        assertEquals(Bill.HUNDRED, billSets.get(0).getBill());
    }

    @Test
    public void withdrawBillsNotEnoughCashAvailable() {
        FundsStorage fundsStorage = new FundsStorage(randomFun, onlyFewTens);
        ATMService atmService = new ATMService(fundsStorage);

        List<BillSet> billSets = atmService.withdrawBills(new WithdrawBillsInput(100, "123"));

        // even though I requested 100, I only expect to get 5 10s as
        assertEquals(1, billSets.size());
        assertEquals(5, billSets.get(0).getNr());
        assertEquals(Bill.TEN, billSets.get(0).getBill());
    }
}

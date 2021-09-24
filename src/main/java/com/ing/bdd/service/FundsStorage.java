package com.ing.bdd.service;

import com.ing.bdd.model.Bill;
import com.ing.bdd.model.BillSet;
import com.ing.bdd.tailrecursion.TailCall;
import io.vavr.collection.List;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static com.ing.bdd.tailrecursion.TailCalls.done;

@RequiredArgsConstructor
public class FundsStorage {
    private final BiFunction<Integer,Integer,Integer> random;
    private final Map<Bill, Integer> billsPresent;

    private final Map<String, Integer> database = new HashMap<>();

    public Integer retrieveCurrentBalance(String accountNr) {
        return database
            .computeIfAbsent(accountNr, i -> initializeAccount());
    }

    private Integer initializeAccount() {
        return random.apply(100, 5000);
    }

    public synchronized java.util.List<BillSet> withdrawBills(String accountNr, Integer amountRequested) {
        if (deductAmountRequestedFromBalance(accountNr, amountRequested)) {
            return determineBillSets(Bill.possibleBills(), List.empty(), amountRequested).invoke().toJavaList();
        } else {
            return Collections.emptyList();
        }
    }

    private boolean deductAmountRequestedFromBalance(String accountNr, Integer amountRequested) {
        Integer currentFunds = database.computeIfAbsent(accountNr, i -> initializeAccount());

        if (currentFunds >= amountRequested) {
            database.put(accountNr, currentFunds - amountRequested);
            return true;
        } else {
            return false;
        }
    }

    private TailCall<List<BillSet>> determineBillSets(List<Bill> possibleBills,
                                                      List<BillSet> billSets,
                                                      Integer amount) {
        if (possibleBills.isEmpty() || amount == 0) {
            return done(billSets);
        } else {
            Bill bill = possibleBills.head();
            BillSet billSet = takeBills(bill, amount);
            return () -> determineBillSets(possibleBills.tail(),
                billSet.getNr() > 0 ? billSets.append(billSet) : billSets,
                amount - billSet.getBill().getIntValue() * billSet.getNr()
            );
        }
    }

    private BillSet takeBills(Bill bill, Integer amount) {
        int nrOfBillsWanted = amount/bill.getIntValue();
        int nrOfBillsTaken = Math.min(nrOfBillsWanted, billsPresent.get(bill));

        billsPresent.put(bill, billsPresent.get(bill) - nrOfBillsTaken);

        return new BillSet(bill, nrOfBillsTaken);
    }
}

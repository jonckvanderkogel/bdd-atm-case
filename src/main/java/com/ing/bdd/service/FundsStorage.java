package com.ing.bdd.service;

import com.ing.bdd.model.Bill;
import com.ing.bdd.model.BillSet;
import com.ing.bdd.tailrecursion.TailCall;
import graphql.GraphQLError;
import io.vavr.collection.List;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static com.ing.bdd.errors.GraphQLErrorClassification.INSUFFICIENT_FUNDS;
import static com.ing.bdd.graphql.GraphQLUtils.createLeft;
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

    public synchronized Either<GraphQLError, java.util.List<BillSet>> withdrawBills(String accountNr, Integer amountRequested) {
        return deductAmountRequestedFromBalance(accountNr, amountRequested)
            .map(a -> determineBillSets(Bill.possibleBills(), List.empty(), a).invoke().toJavaList());
    }

    private Either<GraphQLError, Integer> deductAmountRequestedFromBalance(String accountNr, Integer amountRequested) {
        Integer currentFunds = database.computeIfAbsent(accountNr, i -> initializeAccount());

        if (currentFunds >= amountRequested) {
            database.put(accountNr, currentFunds - amountRequested);
            return Either.right(amountRequested);
        } else {
            return createLeft(INSUFFICIENT_FUNDS, String.valueOf(amountRequested));
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

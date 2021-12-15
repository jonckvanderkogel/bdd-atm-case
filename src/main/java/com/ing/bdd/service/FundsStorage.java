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

import static com.ing.bdd.errors.GraphQLErrorClassification.INSUFFICENT_BILLS_PRESENT;
import static com.ing.bdd.errors.GraphQLErrorClassification.INSUFFICIENT_FUNDS;
import static com.ing.bdd.graphql.GraphQLUtils.createLeft;
import static com.ing.bdd.tailrecursion.TailCalls.done;

@RequiredArgsConstructor
public class FundsStorage {
    private final BiFunction<Integer, Integer, Integer> random;
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
        return determineBillsPresent(Bill.possibleBills(), List.empty(), amountRequested).invoke()
            .flatMap(b -> checkBalanceSufficient(accountNr, amountRequested, b))
            .map(this::removeBillsFromATM)
            .map(b -> deductBillsFromBalance(accountNr, b).toJavaList());
    }

    public synchronized Either<GraphQLError, java.util.List<BillSet>> withdrawBillsWithFees(String accountNr, Integer amountRequested, Integer fee) {
        return determineBillsPresent(Bill.possibleBills(), List.empty(), amountRequested).invoke()
            .flatMap(b -> checkBalanceSufficient(accountNr, amountRequested + fee, b))
            .map(this::removeBillsFromATM)
            .map(b -> deductBillsWithFeeFromBalance(accountNr, b, fee).toJavaList());
    }

    private TailCall<Either<GraphQLError, List<BillSet>>> determineBillsPresent(List<Bill> possibleBills,
                                                                                List<BillSet> billSets,
                                                                                Integer amount) {
        if (amount == 0) {
            return done(Either.right(billSets));
        } else if (possibleBills.isEmpty()) {
            return done(createLeft(INSUFFICENT_BILLS_PRESENT, ""));
        } else {
            Bill bill = possibleBills.head();
            BillSet billSet = determineBillsToTake(bill, amount);
            return () -> determineBillsPresent(possibleBills.tail(),
                billSet.getNr() > 0 ? billSets.append(billSet) : billSets,
                amount - billSet.getBill().getIntValue() * billSet.getNr()
            );
        }
    }

    private BillSet determineBillsToTake(Bill bill, Integer amount) {
        int nrOfBillsWanted = amount / bill.getIntValue();
        int nrOfBillsToTake = Math.min(nrOfBillsWanted, billsPresent.get(bill));

        return new BillSet(bill, nrOfBillsToTake);
    }

    private Either<GraphQLError, List<BillSet>> checkBalanceSufficient(String accountNr, Integer amountRequested, List<BillSet> billSets) {
        Integer currentFunds = database.computeIfAbsent(accountNr, i -> initializeAccount());

        if (currentFunds >= determineValueOfBillSets(billSets)) {
            return Either.right(billSets);
        } else {
            return createLeft(INSUFFICIENT_FUNDS, String.valueOf(amountRequested));
        }
    }

    private Integer determineValueOfBillSets(List<BillSet> billSets) {
        return billSets
            .map(b -> b.getBill().getIntValue() * b.getNr())
            .reduce(Integer::sum);
    }

    private List<BillSet> removeBillsFromATM(List<BillSet> billSets) {
        billSets.forEach(b -> billsPresent.put(b.getBill(), billsPresent.get(b.getBill()) - b.getNr()));

        return billSets;
    }

    private List<BillSet> deductBillsFromBalance(String accountNr, List<BillSet> billSets) {
        deductAmountFromBalance(accountNr, determineValueOfBillSets(billSets));

        return billSets;
    }

    private List<BillSet> deductBillsWithFeeFromBalance(String accountNr, List<BillSet> billSets, Integer fee) {
        deductAmountFromBalance(accountNr, determineValueOfBillSets(billSets) + fee);

        return billSets;
    }

    private void deductAmountFromBalance(String accountNr, Integer amountToDeduct) {
        Integer currentFunds = database.computeIfAbsent(accountNr, i -> initializeAccount());
        database.put(accountNr, currentFunds - amountToDeduct);
    }
}

package com.ing.bdd.service;

import com.ing.bdd.model.BillSetWrapper;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@RequiredArgsConstructor
public class FeeCalculator {
    private final FundsStorage fundsStorage;
    private final BiFunction<Integer,Integer,Integer> random;
    private final Map<String, Integer> database = new HashMap<>();

    private Integer initializeCounter() {
        return 0;
    }

    public synchronized BillSetWrapper withdrawBillsWithFees(String accountNr, Integer amountRequested) {
        Integer amountToDeduct = calculateAmountIncludingFees(accountNr, amountRequested);
        BillSetWrapper result = withdrawBills(accountNr, amountToDeduct);
        if (withdrawSuccess(result)) {
            updateWithdrawalCounter(accountNr);
        }
        return result;
    }

    private BillSetWrapper withdrawBills(String accountNr, Integer amountToDeduct) {
        if (atmCrashes()) {
            return new BillSetWrapper("ATM crashed!");
        }
        return new BillSetWrapper(fundsStorage.withdrawBills(accountNr, amountToDeduct));
    }

    private Integer calculateAmountIncludingFees(String accountNr, Integer amountRequested) {
        Integer amountOfWithdrawals = database.computeIfAbsent(accountNr, i -> initializeCounter());
        return amountOfWithdrawals >= 1 ? amountRequested / 50 + amountRequested + 1: amountRequested;
    }

    private boolean atmCrashes() {
        return random.apply(0, 10) > 8;
    }

    private void updateWithdrawalCounter(String accountNr) {
        database.compute(accountNr, (k, v) -> ++v);
    }

    private boolean withdrawSuccess(BillSetWrapper billSetWrapper) {
        return billSetWrapper.getError().isPresent() || !billSetWrapper.getBillSets().isEmpty();
    }
}

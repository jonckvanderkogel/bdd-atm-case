package com.ing.bdd.service;

import com.ing.bdd.model.BillSetWrapper;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.SplittableRandom;

@RequiredArgsConstructor
public class WithdrawTracker {
    private final SplittableRandom random;

    private final FundsStorage fundsStorage;
    private final Map<String, Integer> database = new HashMap<>();

    private Integer initializeCounter() {
        return 0;
    }

    public synchronized BillSetWrapper withdrawBillsWithFees(String accountNr, Integer amountRequested) {
        BillSetWrapper result;
        Integer amountToDeduct = calculateAmountIncludingFees(accountNr, amountRequested);
        if (atmCrashes()) {
            result = new BillSetWrapper("ATM crashed!");
        } else {
            result = new BillSetWrapper(fundsStorage.withdrawBills(accountNr, amountToDeduct));
        }
        if (withdrawSuccess(result)) {
            updateWithdrawCounter(accountNr);
        }
        return result;
    }

    private Integer calculateAmountIncludingFees(String accountNr, Integer amountRequested) {
        Integer amountOfWithdraws = database.computeIfAbsent(accountNr, i -> initializeCounter());
        return amountOfWithdraws >= 1 ? amountRequested / 50 + amountRequested : amountRequested;
    }

    private boolean atmCrashes() {
        return random.nextInt(0, 100) > 80;
    }

    private void updateWithdrawCounter(String accountNr) {
        database.compute(accountNr, (k, v) -> ++v);
    }

    private boolean withdrawSuccess(BillSetWrapper billSetWrapper) {
        return billSetWrapper.getError().isPresent() || !billSetWrapper.getBillSets().isEmpty();
    }
}

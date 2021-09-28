package com.ing.bdd.service;

import com.ing.bdd.model.BillSet;
import graphql.GraphQLError;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static com.ing.bdd.errors.GraphQLErrorClassification.UPSTREAM_SERVICE_FAILED;
import static com.ing.bdd.graphql.GraphQLUtils.createLeft;

@RequiredArgsConstructor
public class FeeCalculator {
    private final FundsStorage fundsStorage;
    private final BiFunction<Integer, Integer, Integer> random;
    private final Map<String, Integer> database = new HashMap<>();

    private Integer initializeCounter() {
        return 0;
    }

    public synchronized Either<GraphQLError, List<BillSet>> withdrawBillsWithFees(String accountNr, Integer amountRequested) {
        Integer amountToDeduct = calculateAmountIncludingFees(accountNr, amountRequested);
        Either<GraphQLError, List<BillSet>> result = withdrawBills(accountNr, amountToDeduct);

        if (withdrawSuccess(result)) {
            updateWithdrawalCounter(accountNr);
        }
        return result;
    }

    private Either<GraphQLError, List<BillSet>> withdrawBills(String accountNr, Integer amountToDeduct) {
        if (atmCrashes()) {
            return createLeft(UPSTREAM_SERVICE_FAILED, "ATM");
        }
        return fundsStorage.withdrawBills(accountNr, amountToDeduct);
    }

    private Integer calculateAmountIncludingFees(String accountNr, Integer amountRequested) {
        Integer amountOfWithdrawals = database.computeIfAbsent(accountNr, i -> initializeCounter());
        return amountOfWithdrawals >= 1 ? amountRequested / 50 + amountRequested + 1 : amountRequested;
    }

    private boolean atmCrashes() {
        return random.apply(0, 10) > 8;
    }

    private void updateWithdrawalCounter(String accountNr) {
        database.compute(accountNr, (k, v) -> ++v);
    }

    private boolean withdrawSuccess(Either<GraphQLError, List<BillSet>> result) {
        return result.isRight() || result.isLeft();
    }
}

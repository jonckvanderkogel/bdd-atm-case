package com.ing.bdd.service;

import com.ing.bdd.model.BillSet;
import com.ing.bdd.model.SimpleEither;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
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

    public synchronized SimpleEither<GraphQLError, List<BillSet>> withdrawBillsWithFees(String accountNr, Integer amountRequested) {
        Integer amountToDeduct = calculateAmountIncludingFees(accountNr, amountRequested);
        SimpleEither<GraphQLError, List<BillSet>> result = withdrawBills(accountNr, amountToDeduct);
        if (withdrawSuccess(result)) {
            updateWithdrawalCounter(accountNr);
        }
        return result;
    }

    private SimpleEither<GraphQLError, List<BillSet>> withdrawBills(String accountNr, Integer amountToDeduct) {
        if (atmCrashes()) {
            return SimpleEither.error(GraphqlErrorBuilder
                .newError()
                .message("ATM crashed!")
                .build());
        }
        return SimpleEither.success(fundsStorage.withdrawBills(accountNr, amountToDeduct));
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

    private boolean withdrawSuccess(SimpleEither<GraphQLError, List<BillSet>> result) {
        return result.isSuccess() || result.isError();
    }
}

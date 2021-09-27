package com.ing.bdd.service;

import com.ing.bdd.model.Balance;
import com.ing.bdd.model.BillSet;
import com.ing.bdd.model.WithdrawBillsInput;
import graphql.GraphQLError;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ATMService {
    private final FundsStorage fundsStorage;
    private final FeeCalculator feeCalculator;

    public Balance retrieveBalance(String accountNr) {
        return new Balance(
            fundsStorage.retrieveCurrentBalance(accountNr),
            OffsetDateTime.now()
        );
    }

    public Either<GraphQLError,List<BillSet>> withdrawBills(WithdrawBillsInput input) {
        return fundsStorage.withdrawBills(input.getAccountNr(), input.getAmount());
    }

    public Either<GraphQLError, List<BillSet>> withdrawBillsWithFees(WithdrawBillsInput input) {
        return feeCalculator.withdrawBillsWithFees(input.getAccountNr(), input.getAmount());
    }
}

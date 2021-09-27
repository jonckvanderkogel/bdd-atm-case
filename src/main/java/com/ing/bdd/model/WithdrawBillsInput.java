package com.ing.bdd.model;

import graphql.GraphqlErrorBuilder;
import io.vavr.control.Either;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static com.ing.bdd.errors.GraphQLErrorClassification.*;
import static com.ing.bdd.graphql.GraphQLUtils.createLeft;

@Getter
@RequiredArgsConstructor
public class WithdrawBillsInput {
    private final Integer amount;
    private final String accountNr;

    public static Either<graphql.GraphQLError, WithdrawBillsInput> of(Map<String, Object> map) {
        try {
            Integer amount = (Integer) map.get("amount");
            String accountNr = (String) map.get("accountNr");

            return Either.right(new WithdrawBillsInput(amount, accountNr));
        } catch (Exception e) {
            return createLeft(INVALID_INPUT, "amount and accountNr");
        }
    }
}

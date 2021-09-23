package com.ing.bdd.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public class WithdrawBillsInput {
    private final Integer amount;
    private final String accountNr;

    public static Optional<WithdrawBillsInput> of(Map<String, Object> map) {
        try {
            Integer amount = (Integer) map.get("amount");
            String accountNr = (String) map.get("accountNr");

            return Optional.of(new WithdrawBillsInput(amount, accountNr));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}

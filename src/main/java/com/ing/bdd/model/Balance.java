package com.ing.bdd.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@RequiredArgsConstructor
public class Balance {
    private final Integer amount;
    private final OffsetDateTime balanceDate;
}

package com.ing.bdd.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@RequiredArgsConstructor
public class BillSet {
    private final Bill bill;
    private final Integer nr;
}

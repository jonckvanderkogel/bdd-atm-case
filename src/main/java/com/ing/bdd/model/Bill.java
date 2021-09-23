package com.ing.bdd.model;

import io.vavr.collection.List;
import lombok.Getter;

@Getter
public enum Bill {
    TEN(10),TWENTY(20),FIFTY(50),HUNDRED(100);

    private final Integer intValue;

    Bill(Integer intValue) {
        this.intValue = intValue;
    }

    public static List<Bill> possibleBills() {
        return List.of(Bill.values());
    }
}

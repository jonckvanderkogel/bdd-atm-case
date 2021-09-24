package com.ing.bdd.testutil;

import com.ing.bdd.model.Bill;

import java.util.HashMap;
import java.util.Map;

public class Util {
    public static Map<Bill, Integer> generateBillMap(Integer tens, Integer twenties, Integer fifties, Integer hundreds) {
        return new HashMap<>(
            Map.of(
                Bill.TEN, tens,
                Bill.TWENTY, twenties,
                Bill.FIFTY, fifties,
                Bill.HUNDRED, hundreds
            )
        );
    }
}

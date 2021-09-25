package com.ing.bdd.testutil;

import lombok.RequiredArgsConstructor;

import java.util.function.BiFunction;

@RequiredArgsConstructor
public class FaultyAtmBiFunction implements BiFunction<Integer, Integer, Integer> {
    private Integer value = 9;

    @Override
    public Integer apply(Integer lower, Integer upper) {
        return value == 9 ? value-- : value;
    }
}

package com.ing.bdd.config;

import com.ing.bdd.model.Bill;
import com.ing.bdd.service.FundsStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.SplittableRandom;

@Configuration
public class AvailableFundsConfig {
    @Bean
    public FundsStorage fundsStorage() {
        SplittableRandom random = new SplittableRandom();
        Map<Bill, Integer> map =new HashMap<>(
            Map.of(
                Bill.TEN, random.nextInt(0, 1000),
                Bill.TWENTY, random.nextInt(0, 1000),
                Bill.FIFTY, random.nextInt(0, 1000),
                Bill.HUNDRED, random.nextInt(0, 1000)
            )
        );

        return new FundsStorage(random, map);
    }
}

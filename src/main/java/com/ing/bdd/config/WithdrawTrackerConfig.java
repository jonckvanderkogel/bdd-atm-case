package com.ing.bdd.config;

import com.ing.bdd.service.FundsStorage;
import com.ing.bdd.service.WithdrawTracker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.SplittableRandom;

@Configuration
public class WithdrawTrackerConfig {
    @Bean
    public WithdrawTracker withdrawTracker(FundsStorage fundsStorage) {
        SplittableRandom random = new SplittableRandom();
        return new WithdrawTracker(random, fundsStorage);
    }
}

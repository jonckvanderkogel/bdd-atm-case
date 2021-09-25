package com.ing.bdd.config;

import com.ing.bdd.service.FeeCalculator;
import com.ing.bdd.service.FundsStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.SplittableRandom;

@Configuration
public class FeeCalculatorConfig {
    @Bean
    public FeeCalculator feeCalculator(FundsStorage fundsStorage) {
        SplittableRandom random = new SplittableRandom();
        return new FeeCalculator(fundsStorage, random::nextInt);
    }
}

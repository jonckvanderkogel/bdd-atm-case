package com.ing.bdd.graphql;

import com.ing.bdd.model.Balance;
import com.ing.bdd.model.BillSet;
import com.ing.bdd.model.WithdrawBillsInput;
import com.ing.bdd.service.ATMService;
import graphql.GraphQLError;
import graphql.execution.DataFetcherResult;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.ing.bdd.graphql.GraphQLUtils.errorFun;
import static com.ing.bdd.graphql.GraphQLUtils.successFun;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class GraphQLDataFetchers {
    private final ATMService atmService;

    @Bean
    public DataFetcherWrapper<Balance> retrieveBalance() {
        return new DataFetcherWrapper<>(
            "Query",
            "retrieveBalance",
            dataFetchingEnvironment -> {
                String accountNr = dataFetchingEnvironment.getArgument("accountNr");
                return atmService.retrieveBalance(accountNr);
            }
        );
    }

    @Bean
    public DataFetcherWrapper<DataFetcherResult<List<BillSet>>> withdrawBills() {
        return new DataFetcherWrapper<>(
            "Mutation",
            "withdrawBills",
            dataFetchingEnvironment -> {
                Map<String, Object> withdrawBillsInputMap = dataFetchingEnvironment.getArgument("withdrawBillsInput");
                return withdrawBillsInputInteraction(withdrawBillsInputMap, atmService::withdrawBills);
            }
        );
    }

    @Bean
    public DataFetcherWrapper<DataFetcherResult<List<BillSet>>> withdrawBillsWithFees() {
        return new DataFetcherWrapper<>(
            "Mutation",
            "withdrawBillsWithFees",
            dataFetchingEnvironment -> {
                Map<String, Object> withdrawBillsInputMap = dataFetchingEnvironment.getArgument("withdrawBillsInput");
                return withdrawBillsInputInteraction(withdrawBillsInputMap, atmService::withdrawBillsWithFees);
            }
        );
    }

    private <T> DataFetcherResult<T> withdrawBillsInputInteraction(Map<String, Object> inputMap,
                                                                   Function<WithdrawBillsInput, Either<GraphQLError, T>> fun) {
        return WithdrawBillsInput
            .of(inputMap)
            .flatMap(fun)
            .fold(
                errorFun(),
                successFun()
            );
    }

}

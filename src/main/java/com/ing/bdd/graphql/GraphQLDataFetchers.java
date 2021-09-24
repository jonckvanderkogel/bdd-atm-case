package com.ing.bdd.graphql;

import com.ing.bdd.model.Balance;
import com.ing.bdd.model.Bill;
import com.ing.bdd.model.BillSet;
import com.ing.bdd.model.BillSetWrapper;
import com.ing.bdd.model.WithdrawBillsInput;
import com.ing.bdd.service.ATMService;
import graphql.execution.DataFetcherResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
    public DataFetcherWrapper<List<BillSet>> withdrawBills() {
        return new DataFetcherWrapper<>(
            "Mutation",
            "withdrawBills",
            dataFetchingEnvironment -> {
                Map<String, Object> withdrawBillsInputMap = dataFetchingEnvironment.getArgument("withdrawBillsInput");

                return WithdrawBillsInput
                    .of(withdrawBillsInputMap)
                    .map(atmService::withdrawBills)
                    .orElseGet(() -> List.of(new BillSet(Bill.TEN, 0)));
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
                DataFetcherResult.Builder<List<BillSet>> resultBuilder = DataFetcherResult.newResult();
                BillSetWrapper billSetWrapper = WithdrawBillsInput
                    .of(withdrawBillsInputMap)
                    .map(atmService::withdrawBillsWithFees)
                    .orElseGet(() -> new BillSetWrapper(List.of(new BillSet(Bill.TEN, 0))));
                billSetWrapper.getError().ifPresent(resultBuilder::error);

                return resultBuilder
                    .data(billSetWrapper.getBillSets())
                    .build();
            }
        );
    }
}

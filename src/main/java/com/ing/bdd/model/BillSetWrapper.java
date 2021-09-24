package com.ing.bdd.model;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
public class BillSetWrapper {

    private final List<BillSet> billSets;
    private final Optional<GraphQLError> error;

    public BillSetWrapper(List<BillSet> billSets) {
        this.billSets = billSets;
        this.error = Optional.empty();
    }

    public BillSetWrapper(String error) {
        this.billSets = Collections.emptyList();
        this.error = Optional.of(GraphqlErrorBuilder.newError().message(error).build());
    }
}

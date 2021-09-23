package com.ing.bdd.graphql;

import graphql.GraphQLError;
import graphql.execution.DataFetcherResult;

import java.util.function.Function;

public class GraphQLUtils {
    public static <T> Function<T, DataFetcherResult<T>> successFun() {
        return r -> DataFetcherResult.<T>newResult().data(r).build();
    }

    public static <T> Function<GraphQLError, DataFetcherResult<T>> errorFun() {
        return l -> DataFetcherResult.<T>newResult().error(l).build();
    }
}

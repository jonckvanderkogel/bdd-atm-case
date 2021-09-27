package com.ing.bdd.graphql;

import com.ing.bdd.errors.GraphQLErrorClassification;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.execution.DataFetcherResult;
import io.vavr.control.Either;

import java.util.function.Function;

public class GraphQLUtils {
    public static <T> Function<T, DataFetcherResult<T>> successFun() {
        return r -> DataFetcherResult.<T>newResult().data(r).build();
    }

    public static <T> Function<GraphQLError, DataFetcherResult<T>> errorFun() {
        return l -> DataFetcherResult.<T>newResult().error(l).build();
    }

    public static <T> Either<GraphQLError, T> createLeft(GraphQLErrorClassification classification, String errorArg) {
        return Either.left(GraphqlErrorBuilder
            .newError()
            .message(classification.getErrorMessage(errorArg))
            .errorType(classification)
            .build());
    }
}

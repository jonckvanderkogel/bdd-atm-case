package com.ing.bdd.errors;

import graphql.ErrorClassification;

import java.util.function.Function;

public enum GraphQLErrorClassification implements ErrorClassification {
    INVALID_INPUT(fun("The input must contain %s.")),
    UPSTREAM_SERVICE_FAILED(fun("Upstream service \"%s\" failed.")),
    INSUFFICENT_BILLS_PRESENT(fun("The amount is not available.")),
    INSUFFICIENT_FUNDS(fun("Insufficient balance to withdraw %s Euros."));

    private final Function<String, String> errorMessageFun;

    GraphQLErrorClassification(Function<String, String> errorMessageFun) {
        this.errorMessageFun = errorMessageFun;
    }

    public String getErrorMessage(String arg) {
        return errorMessageFun.apply(arg);
    }

    private static Function<String, String> fun(String templateText) {
        return arg -> String.format(templateText, arg);
    }
}

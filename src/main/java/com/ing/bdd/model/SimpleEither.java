package com.ing.bdd.model;

import java.util.NoSuchElementException;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public interface SimpleEither<T, S> {

    static <T, S> SimpleEither<T, S> success(S success) {
        return new Success<>(success);
    }
    static <T, S> SimpleEither<T, S> error(T error) {
        return new Error<>(error);
    }

    boolean isError();
    boolean isSuccess();
    S getSuccess();
    T getError();

    default <U> U fold(Function<? super T, ? extends U> errorMapper, Function<? super S, ? extends U> successMapper) {
        requireNonNull(errorMapper, "errorMapper is null");
        requireNonNull(successMapper, "successMapper is null");
        if (isSuccess()) {
            return successMapper.apply(getSuccess());
        } else {
            return errorMapper.apply(getError());
        }
    }

    final class Success<T, S> implements SimpleEither<T, S> {

        private final S value;

        private Success(S value) {
            this.value = value;
        }

        @Override
        public boolean isError() {
            return false;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public S getSuccess() {
            return value;
        }

        @Override
        public T getError() {
            throw new NoSuchElementException("getError() on Data");
        }
    }

    final class Error<T, S> implements SimpleEither<T, S> {

        private final T value;

        private Error(T value) {
            this.value = value;
        }

        @Override
        public boolean isError() {
            return true;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public S getSuccess() {
            throw new NoSuchElementException("getSuccess() on Error");
        }

        @Override
        public T getError() {
            return value;
        }
    }
}

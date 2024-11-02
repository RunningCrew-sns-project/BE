package com.github.accountmanagementproject.exception;

import lombok.Getter;

@Getter
public class StorageDeleteFailedException extends MakeRuntimeException {

    private StorageDeleteFailedException(ExceptionBuilder exceptionBuilder) {
        super(exceptionBuilder);
    }

    public static class ExceptionBuilder extends MakeRuntimeException.ExceptionBuilder<ExceptionBuilder> {

        @Override
        protected ExceptionBuilder self() {
            return this;
        }

        @Override
        public StorageDeleteFailedException build() {
            return new StorageDeleteFailedException(this);
        }
    }
}

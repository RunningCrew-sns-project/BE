package com.github.accountmanagementproject.exception;

public class StorageUpdateFailedException extends MakeRuntimeException{
    private StorageUpdateFailedException(StorageUpdateFailedException.ExceptionBuilder exceptionBuilder) {
        super(exceptionBuilder);
    }

    public static class ExceptionBuilder extends MakeRuntimeException.ExceptionBuilder<StorageUpdateFailedException.ExceptionBuilder>{

        @Override
        protected StorageUpdateFailedException.ExceptionBuilder self() {
            return this;
        }
        @Override
        public StorageUpdateFailedException build() {
            return new StorageUpdateFailedException(this);
        }
    }
}

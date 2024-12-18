package com.github.accountmanagementproject.exception;

import lombok.Getter;

@Getter
public abstract class MakeRuntimeException extends RuntimeException {
    private final String customMessage;
    private final Object request;

    protected MakeRuntimeException(ExceptionBuilder<?> exceptionBuilder) {
        super(exceptionBuilder.systemMessage);
        this.customMessage = exceptionBuilder.customMessage;
        this.request = exceptionBuilder.request;
    }

    public abstract static class ExceptionBuilder<T extends ExceptionBuilder<T>> {
        private String systemMessage;
        private String customMessage;
        private Object request;

        public T systemMessage(String systemMessage) {
            this.systemMessage = systemMessage;
            return self();
        }

        public T customMessage(String customMessage) {
            this.customMessage = customMessage;
            return self();
        }

        public T request(Object request) {
            this.request = request;
            return self();
        }

        protected abstract T self();

        public abstract MakeRuntimeException build();
    }
}

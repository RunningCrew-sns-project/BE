package com.github.accountmanagementproject.exception;

public class CustomServerException  extends MakeRuntimeException{

    private CustomServerException(ExceptionBuilder exceptionBuilder) {
        super(exceptionBuilder);
    }
    public static class ExceptionBuilder extends MakeRuntimeException.ExceptionBuilder<ExceptionBuilder>{
        @Override
        protected ExceptionBuilder self() {
            return this;
        }
        @Override
        public CustomServerException build() {
            return new CustomServerException(this);
        }
    }

}
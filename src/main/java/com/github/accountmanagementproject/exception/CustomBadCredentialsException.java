package com.github.accountmanagementproject.exception;

import lombok.Getter;

@Getter
public class CustomBadCredentialsException extends MakeRuntimeException{

    private CustomBadCredentialsException(ExceptionBuilder exceptionBuilder) {
        super(exceptionBuilder);
    }
    public static class ExceptionBuilder extends MakeRuntimeException.ExceptionBuilder<ExceptionBuilder>{

        @Override
        protected ExceptionBuilder self() {
            return this;
        }
        @Override
        public CustomBadCredentialsException build() {
            return new CustomBadCredentialsException(this);
        }
    }

}

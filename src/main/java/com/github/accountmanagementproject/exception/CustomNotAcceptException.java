package com.github.accountmanagementproject.exception;

import lombok.Getter;

@Getter
public class CustomNotAcceptException extends MakeRuntimeException{

    private CustomNotAcceptException(ExceptionBuilder exceptionBuilder) {
        super(exceptionBuilder);
    }


    public static class ExceptionBuilder extends MakeRuntimeException.ExceptionBuilder<ExceptionBuilder>{

        @Override
        protected ExceptionBuilder self() {
            return this;
        }
        @Override
        public CustomNotAcceptException build() {
            return new CustomNotAcceptException(this);
        }
    }

}

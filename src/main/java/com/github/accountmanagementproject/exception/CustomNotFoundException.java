package com.github.accountmanagementproject.exception;

import lombok.Getter;

@Getter
public class CustomNotFoundException extends MakeRuntimeException{

    private CustomNotFoundException(ExceptionBuilder exceptionBuilder) {
        super(exceptionBuilder);
    }


    public static class ExceptionBuilder extends MakeRuntimeException.ExceptionBuilder<ExceptionBuilder>{

        @Override
        protected ExceptionBuilder self() {
            return this;
        }
        @Override
        public CustomNotFoundException build() {
            return new CustomNotFoundException(this);
        }
    }

}

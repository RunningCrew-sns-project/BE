package com.github.accountmanagementproject.exception;

import lombok.Getter;

@Getter
public class CustomBadRequestException extends MakeRuntimeException{

    private CustomBadRequestException(ExceptionBuilder exceptionBuilder) {
        super(exceptionBuilder);
    }


    public static class ExceptionBuilder extends MakeRuntimeException.ExceptionBuilder<ExceptionBuilder>{

        @Override
        protected ExceptionBuilder self() {
            return this;
        }
        @Override
        public CustomBadRequestException build() {
            return new CustomBadRequestException(this);
        }
    }

}

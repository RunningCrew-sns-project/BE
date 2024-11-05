package com.github.runningcrewsnsproject.exception;

import lombok.Getter;

@Getter
public class CustomAccessDenied extends MakeRuntimeException{

    private CustomAccessDenied(ExceptionBuilder exceptionBuilder) {
        super(exceptionBuilder);
    }
    public static class ExceptionBuilder extends MakeRuntimeException.ExceptionBuilder<ExceptionBuilder>{
        @Override
        protected ExceptionBuilder self() {
            return this;
        }
        @Override
        public CustomAccessDenied build() {
            return new CustomAccessDenied(this);
        }
    }

}

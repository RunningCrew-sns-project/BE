package com.github.runningcrewsnsproject.exception;

import lombok.Getter;

@Getter
public class DuplicateKeyException extends MakeRuntimeException{

    private DuplicateKeyException(ExceptionBuilder exceptionBuilder) {
        super(exceptionBuilder);
    }


    public static class ExceptionBuilder extends MakeRuntimeException.ExceptionBuilder<ExceptionBuilder>{

        @Override
        protected ExceptionBuilder self() {
            return this;
        }
        @Override
        public DuplicateKeyException build() {
            return new DuplicateKeyException(this);
        }
    }

}

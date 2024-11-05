package com.github.runningcrewsnsproject.exception;

import lombok.Getter;


@Getter
public class ResourceNotFoundException extends MakeRuntimeException {

    private ResourceNotFoundException(ExceptionBuilder exceptionBuilder) {
        super(exceptionBuilder);
    }

    public static class ExceptionBuilder extends MakeRuntimeException.ExceptionBuilder<ExceptionBuilder> {

        @Override
        protected ExceptionBuilder self() {
            return this;
        }

        @Override
        public ResourceNotFoundException build() {
            return new ResourceNotFoundException(this);
        }
    }
}

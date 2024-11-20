package com.github.accountmanagementproject.exception;

import lombok.Getter;

@Getter
public class NotFoundSocialAccount extends MakeRuntimeException{
    private NotFoundSocialAccount(NotFoundSocialAccount.ExceptionBuilder exceptionBuilder) {
        super(exceptionBuilder);
    }

    public static class ExceptionBuilder extends MakeRuntimeException.ExceptionBuilder<NotFoundSocialAccount.ExceptionBuilder>{

        @Override
        protected NotFoundSocialAccount.ExceptionBuilder self() {
            return this;
        }
        @Override
        public NotFoundSocialAccount build() {
            return new NotFoundSocialAccount(this);
        }
    }
}

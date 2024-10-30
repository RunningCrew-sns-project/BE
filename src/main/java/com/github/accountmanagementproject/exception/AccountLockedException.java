package com.github.accountmanagementproject.exception;



public class AccountLockedException extends MakeRuntimeException{

    private AccountLockedException(ExceptionBuilder exceptionBuilder) {
        super(exceptionBuilder);
    }


    public static class ExceptionBuilder extends MakeRuntimeException.ExceptionBuilder<ExceptionBuilder>{

        @Override
        protected ExceptionBuilder self() {
            return this;
        }
        @Override
        public AccountLockedException build() {
            return new AccountLockedException(this);
        }
    }

}

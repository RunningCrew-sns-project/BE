package com.github.accountmanagementproject.service.customExceptions;

import com.github.accountmanagementproject.web.dto.accountAuth.oauth.SocialAccountDto;
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

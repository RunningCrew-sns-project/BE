package com.github.runningcrewsnsproject.service.mapper.converter;

import com.github.runningcrewsnsproject.repository.account.user.myenum.OAuthProvider;

public class OAuthProviderConverter extends MyConverter<OAuthProvider> {
    public static final Class<OAuthProvider> ENUM_CLASS = OAuthProvider.class;

    public OAuthProviderConverter() {
        super(ENUM_CLASS);
    }
}

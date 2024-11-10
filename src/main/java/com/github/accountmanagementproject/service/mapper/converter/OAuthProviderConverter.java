package com.github.accountmanagementproject.service.mapper.converter;

import com.github.accountmanagementproject.repository.account.user.myenum.OAuthProvider;

public class OAuthProviderConverter extends MyConverter<OAuthProvider> {
    public static final Class<OAuthProvider> ENUM_CLASS = OAuthProvider.class;

    public OAuthProviderConverter() {
        super(ENUM_CLASS);
    }
}

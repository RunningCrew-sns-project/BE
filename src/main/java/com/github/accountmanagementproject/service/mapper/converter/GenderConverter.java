package com.github.accountmanagementproject.service.mapper.converter;

import com.github.accountmanagementproject.repository.account.user.myenum.Gender;

public class GenderConverter extends MyConverter<Gender> {


    public GenderConverter(){
        super(Gender.class);
    }

}

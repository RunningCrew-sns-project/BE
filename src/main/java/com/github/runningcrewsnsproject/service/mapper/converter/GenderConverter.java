package com.github.runningcrewsnsproject.service.mapper.converter;

import com.github.runningcrewsnsproject.repository.account.user.myenum.Gender;

public class GenderConverter extends MyConverter<Gender> {
    public static final Class<Gender> ENUM_CLASS = Gender.class;


    public GenderConverter(){
        super(ENUM_CLASS);
    }

}

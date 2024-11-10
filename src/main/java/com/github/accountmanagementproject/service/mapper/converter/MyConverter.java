package com.github.accountmanagementproject.service.mapper.converter;
import com.github.accountmanagementproject.repository.account.user.myenum.MyEnumInterface;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.EnumSet;

@Converter
public abstract class MyConverter<T extends Enum<T> & MyEnumInterface> implements AttributeConverter<T, String>   {

    private final Class<T> targetEnumClass;

    public MyConverter(Class<T> clazz) {
        this.targetEnumClass = clazz;
    }


    @Override//null 인경우 여기로 안옴
    public String convertToDatabaseColumn(T myEnum) {
        return myEnum.getValue();
    }

    @Override
    public T convertToEntityAttribute(String myEnumName) {
        return myEnumName==null ? null : EnumValueToEnum(myEnumName);
    }

    public T EnumValueToEnum(String value){
        for(T myEnum : EnumSet.allOf(targetEnumClass)){
            if(myEnum.getValue().equals(value)) return myEnum;
        }
        return null;
    }



}

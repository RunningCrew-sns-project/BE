package com.github.runningcrewsnsproject.repository.runningPost.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public interface BaseEnum {

    @JsonValue
    String getValue();

    @JsonCreator
    static <E extends Enum<E> & BaseEnum> E fromValue(Class<E> enumType, String value) {
        for (E constant : enumType.getEnumConstants()) {
            if (constant.getValue().equals(value)) {
                return constant;
            }
        }
        throw new IllegalArgumentException("Unknown enum value: " + value);
    }
}

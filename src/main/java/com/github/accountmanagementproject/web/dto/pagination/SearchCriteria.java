package com.github.accountmanagementproject.web.dto.pagination;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum SearchCriteria {
    LATEST("최신"),
    POPULAR("인기"),
    NAME("이름"),
    MEMBER("멤버"),
    ACTIVITIES("활동");
    private final String value;

    @JsonValue
    public String getValue() {
        return this.value;
    }
    @JsonCreator
    public static SearchCriteria inValue(String criteria){
        try {
            return criteria != null ? SearchCriteria.valueOf(criteria.toUpperCase()) : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}

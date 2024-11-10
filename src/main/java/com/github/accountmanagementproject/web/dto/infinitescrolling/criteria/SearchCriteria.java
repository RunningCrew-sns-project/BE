package com.github.accountmanagementproject.web.dto.infinitescrolling.criteria;


import com.github.accountmanagementproject.repository.account.user.myenum.MyEnumInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SearchCriteria  implements MyEnumInterface {
    LATEST("최신"),
    POPULAR("인기"),
    NAME("이름"),
    MEMBER("멤버"),
    ACTIVITIES("활동");
    private final String value;


    public static SearchCriteria setValue(String criteria){
        try {
            return criteria != null ? SearchCriteria.valueOf(criteria.toUpperCase()) : SearchCriteria.LATEST;
        } catch (IllegalArgumentException e) {
            return LATEST;
        }
    }

}

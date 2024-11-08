package com.github.accountmanagementproject.web.dto.infinitescrolling.criteria;

import com.github.accountmanagementproject.exception.CustomBadRequestException;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class SearchRequest {
    private int size = 20;
    private CursorHolder cursorHolder;
    private String cursor;
    private boolean reverse;
    private SearchCriteria searchCriteria;
    public SearchRequest(int size, boolean reverse, String searchCriteria, String cursor) {
        this.size = size;
        this.reverse = reverse;
        this.searchCriteria = SearchCriteria.setValue(searchCriteria);
        this.cursor = cursor;
    }


    public void makeCursorHolder(){
        try {
            switch (this.searchCriteria) {
                case NAME -> {
                    this.cursorHolder = CursorHolder.fromName(cursor);
                }
                case LATEST -> {
                    this.cursorHolder = CursorHolder.fromCreatedAt(LocalDateTime.parse(cursor, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                }
                case MEMBER -> {
                    this.cursorHolder = CursorHolder.fromMember(Integer.parseInt(cursor));
                }
            }
        }catch (Exception e){
            throw new CustomBadRequestException.ExceptionBuilder()
                    .request(this.searchCriteria.getValue()+" 순서 "+this.cursor).customMessage("정렬 조건에 따른 커서의 값이 잘못 되었습니다.").systemMessage(e.getMessage()).build();
        }
    }

}

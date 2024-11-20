package com.github.accountmanagementproject.web.dto.infinitescrolling.criteria;

import com.github.accountmanagementproject.exception.CustomBadRequestException;
import com.github.accountmanagementproject.service.mapper.converter.CrewRegionConverter;
import com.github.accountmanagementproject.web.dto.crew.CrewRegion;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class SearchRequest {
    private int size;
    private CursorHolder cursorHolder;
    private Long cursorId;
    private String cursor;
    private boolean reverse;
    private CrewRegion crewRegion;
    private SearchCriteria searchCriteria;
    public SearchRequest(int size, boolean reverse, String searchCriteria, String cursor, Long cursorId, String crewRegion) {
        this.size = size;
        this.reverse = reverse;
        this.searchCriteria = SearchCriteria.setValue(searchCriteria);
        this.cursor = cursor;
        this.cursorId = cursorId;
        this.crewRegion = new CrewRegionConverter().convertToEntityAttribute(crewRegion);
    }


    public void makeCursorHolder(){
        try {
            CursorHolder holder = CursorHolder.fromId(cursorId);
            switch (this.searchCriteria) {
                case POPULAR,ACTIVITIES -> {
                    this.cursorHolder = holder.withPopularOrActivitiesCursor(Long.parseLong(cursor));
                }
                case NAME -> {
                    this.cursorHolder = holder.withName(cursor);
                }
                case LATEST -> {
                    this.cursorHolder = holder.withCreatedAt(LocalDateTime.parse(cursor, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                }
                case MEMBER -> {
                    this.cursorHolder = holder.withMember(Long.parseLong(cursor));
                }
            }
        }catch (NullPointerException e){
            throw new CustomBadRequestException.ExceptionBuilder()
                    .request(this.cursor+" 요청 커서의 id "+this.cursorId).customMessage("커서가 존재할때 cursorId는 필수 입니다.").systemMessage(e.getMessage()).build();
        }
        catch (Exception e){
            throw new CustomBadRequestException.ExceptionBuilder()
                    .request(this.searchCriteria.getValue()+" 순서 "+this.cursor).customMessage("정렬 조건에 따른 커서의 값이 잘못 되었습니다.").systemMessage(e.getMessage()).build();
        }
    }

}
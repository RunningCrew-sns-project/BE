package com.github.accountmanagementproject.service.runJoinPost.crewJoinPost;


import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPost;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CrewJoinPostQueryService {

    @PersistenceContext
    private EntityManager entityManager;

    public Slice<CrewJoinPost> findFilteredPosts(LocalDate date, String location, Pageable pageable) {
        // 1단계: 조건에 맞는 게시물 ID만 페이징하여 조회
        String queryStr = "SELECT c.runId FROM CrewJoinPost c " +
                "WHERE (:date IS NULL OR c.date = :date) " +
                "AND (:location IS NULL OR c.location = :location) " +
                "ORDER BY c.createdAt DESC";

        TypedQuery<Long> idQuery = entityManager.createQuery(queryStr, Long.class);
        idQuery.setParameter("date", date);
        idQuery.setParameter("location", location);
        idQuery.setFirstResult((int) pageable.getOffset());
        idQuery.setMaxResults(pageable.getPageSize() + 1);

        List<Long> postIds = idQuery.getResultList();

        // 다음 페이지 존재 여부 확인
        boolean hasNext = postIds.size() > pageable.getPageSize();
        if (hasNext) {
            postIds = postIds.subList(0, pageable.getPageSize()); // 페이지 크기만큼 잘라서 사용
        }

        // 2단계: 페치 조인하여 실제 게시물 데이터 조회
        String fetchQueryStr = "SELECT c FROM CrewJoinPost c LEFT JOIN FETCH c.crewJoinPostImages " +
                "WHERE c.runId IN :postIds " +
                "ORDER BY c.createdAt DESC";

        TypedQuery<CrewJoinPost> fetchQuery = entityManager.createQuery(fetchQueryStr, CrewJoinPost.class);
        fetchQuery.setParameter("postIds", postIds);

        List<CrewJoinPost> posts = fetchQuery.getResultList();

        return new SliceImpl<>(posts, pageable, hasNext);
    }
}

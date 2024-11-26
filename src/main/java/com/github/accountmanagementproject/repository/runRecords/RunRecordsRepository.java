package com.github.accountmanagementproject.repository.runRecords;

import com.github.accountmanagementproject.repository.account.user.MyUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RunRecordsRepository extends JpaRepository<RunRecords, Integer> {
    Optional<RunRecords> findTopByOrderByIdDesc();

    Page<RunRecords> findByIdLessThanOrderByIdDesc(Integer id, Pageable pageable);

    Page<RunRecords> findByUserAndIdLessThanOrderByIdDesc(MyUser user, Integer id, Pageable pageable);
}

package com.github.accountmanagementproject.repository.blog;

import com.github.accountmanagementproject.repository.account.user.MyUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Integer> {
    Page<Blog> findByIdLessThanOrderByIdDesc(Integer id, Pageable pageable);

    Page<Blog> findByUserAndIdLessThanOrderByIdDesc(MyUser user, Integer id, Pageable pageable);

    Blog findTopByOrderByIdDesc();
}

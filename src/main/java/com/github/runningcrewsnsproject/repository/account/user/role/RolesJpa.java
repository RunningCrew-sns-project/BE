package com.github.runningcrewsnsproject.repository.account.user.role;

import com.github.runningcrewsnsproject.repository.account.user.myenum.RolesEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesJpa extends JpaRepository<Role, Integer> {
    Role findByName(RolesEnum name);
}

package com.github.runningcrewsnsproject.repository.account.user.role;

import com.github.runningcrewsnsproject.repository.account.user.MyUser;
import com.github.runningcrewsnsproject.repository.account.user.myenum.RolesEnum;
import com.github.runningcrewsnsproject.service.mapper.converter.RoleConverter;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Set;

@Getter
@Entity
@Table(name = "roles")
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer rolesId;


    @Convert(converter = RoleConverter.class)
    @Column(length = 4)
    private RolesEnum name;

    @ManyToMany(mappedBy = "roles")
    private Set<MyUser> myUsers;

}


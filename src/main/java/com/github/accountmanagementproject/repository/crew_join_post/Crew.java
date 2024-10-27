package com.github.accountmanagementproject.repository.crew_join_post;


import com.github.accountmanagementproject.repository.account.users.MyUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import java.util.List;
import java.util.Set;

@Setter
@Getter
@DynamicInsert
@Entity
@Table(name = "crews")
public class Crew {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crew_id")
    private int id;

    private String crewName;

    private String crewIntroduction;

    private String crewMaster;

    @ManyToMany(mappedBy = "crews") // 'User' 엔티티의 'crews' 필드에 의해 매핑됨
    private Set<MyUser> users;

    @OneToMany(mappedBy = "crew", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CrewJoinPost> crewJoinPostList;

}

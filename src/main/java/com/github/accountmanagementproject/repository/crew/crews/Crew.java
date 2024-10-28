package com.github.accountmanagementproject.repository.crew.crews;

import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.repository.crew.crewAttachments.CrewAttachment;
import com.github.accountmanagementproject.repository.crew.crewImages.CrewImage;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "crews")
@Getter
@Setter
@DynamicInsert
public class Crew {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crew_id")
    private Integer crewId;
    @Column(name = "crew_name", nullable = false, unique = true)
    private String crewName;
    @Column(name = "crew_introduction")
    private String crewIntroduction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_master", nullable = false)
    private MyUser crewMaster;

    @Column(name = "activity_region")
    private String activityRegion;
    @Column(name = "max_capacity")
    private Integer maxCapacity;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "crew", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CrewImage> crewImages;
    @OneToMany(mappedBy = "crew", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CrewAttachment> crewAttachments;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "crews_users",
            joinColumns = @JoinColumn(name = "crew_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<MyUser> crewUsers;

}

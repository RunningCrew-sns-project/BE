package com.github.accountmanagementproject.repository.crew.crew;

import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.crew.crewattachment.CrewAttachment;
import com.github.accountmanagementproject.repository.crew.crewimage.CrewImage;
import com.github.accountmanagementproject.service.mapper.converter.CrewRegionConverter;
import com.github.accountmanagementproject.web.dto.crew.CrewRegion;
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
    private Long crewId;
    @Column(name = "crew_name", nullable = false, unique = true)
    private String crewName;
    @Column(name = "crew_introduction")
    private String crewIntroduction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_master", nullable = false)
    private MyUser crewMaster;

    @Convert(converter = CrewRegionConverter.class)
    private CrewRegion activityRegion;
    @Column(name = "max_capacity")
    private int maxCapacity;
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

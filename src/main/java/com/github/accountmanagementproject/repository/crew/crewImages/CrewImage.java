package com.github.accountmanagementproject.repository.crew.crewImages;

import com.github.accountmanagementproject.repository.crew.crews.Crew;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "crew_images")
@NoArgsConstructor
@Getter
@Setter
public class CrewImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crew_image_id")
    private Long crewImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id", nullable = false)
    private Crew crew;

    @Column(name = "image_url")
    private String imageUrl;


}

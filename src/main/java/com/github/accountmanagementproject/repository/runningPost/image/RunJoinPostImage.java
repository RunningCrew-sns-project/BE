package com.github.accountmanagementproject.repository.runningPost.image;

import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPost;
import com.github.accountmanagementproject.repository.runningPost.generalPost.GeneralJoinPost;
import jakarta.persistence.*;
import lombok.*;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "run_join_post_image")
public class RunJoinPostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_post_id", nullable = false)
    private CrewJoinPost crewJoinPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "general_post_id", nullable = false)
    private GeneralJoinPost generalJoinPost;

}

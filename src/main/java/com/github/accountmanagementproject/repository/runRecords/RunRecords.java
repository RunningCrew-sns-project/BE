package com.github.accountmanagementproject.repository.runRecords;

import com.github.accountmanagementproject.repository.account.user.MyUser;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@Table(name = "run_records")
@NoArgsConstructor
@AllArgsConstructor
public class RunRecords {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private MyUser user;

    @Column(name = "record", length = 30)
    private String record;

    @Column(name = "distance", length = 30)
    private String distance;

    @Column(name = "progress", length = 30)
    private Integer progress;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

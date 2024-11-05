package com.github.accountmanagementproject.repository.crew.crewattachment;

import com.github.accountmanagementproject.repository.crew.crew.Crew;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "crew_attachments")
@Getter
@Setter
@NoArgsConstructor
public class CrewAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crew_attachment_id")
    private Long crewAttachmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id", nullable = false)
    private Crew crew;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "file_url")
    private String fileUrl;


}

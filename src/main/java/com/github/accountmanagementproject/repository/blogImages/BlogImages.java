package com.github.accountmanagementproject.repository.blogImages;

import com.github.accountmanagementproject.repository.blog.Blog;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity(name = "blog_images")
public class BlogImages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "blog_id")
    private Blog blog;

    @Column(name = "image_url")
    @Builder.Default
    private String imageUrl = "https://running-crew.s3.ap-northeast-2.amazonaws.com/default_image/blog_default.jpg";
}

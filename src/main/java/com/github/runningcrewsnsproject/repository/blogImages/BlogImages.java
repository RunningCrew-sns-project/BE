package com.github.runningcrewsnsproject.repository.blogImages;

import com.github.runningcrewsnsproject.repository.blog.Blog;
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
    private String imageUrl;
}

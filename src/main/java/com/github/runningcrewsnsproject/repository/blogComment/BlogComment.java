package com.github.runningcrewsnsproject.repository.blogComment;

import com.github.runningcrewsnsproject.repository.account.user.MyUser;
import com.github.runningcrewsnsproject.repository.blog.Blog;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "blog_comment")
public class BlogComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blog_comment_id", nullable = false)
    private Integer id;

    @Column(name = "content")
    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private MyUser user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

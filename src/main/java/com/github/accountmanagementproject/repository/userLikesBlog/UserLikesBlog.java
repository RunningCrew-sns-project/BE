package com.github.accountmanagementproject.repository.userLikesBlog;

import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.blog.Blog;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_likes_blog")
public class UserLikesBlog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_likes_blog_id")
    private Integer userLikesBlogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id")
    private Blog blog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private MyUser user;
}

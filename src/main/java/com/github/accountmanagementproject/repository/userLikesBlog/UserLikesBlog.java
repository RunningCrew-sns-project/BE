package com.github.accountmanagementproject.repository.userLikesBlog;

import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.repository.blog.Blog;
import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.units.qual.C;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "userLikesBlog")
public class UserLikesBlog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_likes_blog_id")
    private Integer userLikesBlogId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "blog_id")
    private Blog blog;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private MyUser user;
}

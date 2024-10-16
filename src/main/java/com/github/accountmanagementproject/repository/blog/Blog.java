package com.github.accountmanagementproject.repository.blog;

import com.github.accountmanagementproject.repository.account.users.MyUser;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "blog")
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blog_id", nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "record", nullable = false, length = 100)
    private String record;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private MyUser user;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Blog blog)) return false;
        return Objects.equals(id, blog.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Blog{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", record='" + record + '\'' +
                ", image='" + image + '\'' +
                ", user=" + user.getNickname() +
                '}';
    }
}
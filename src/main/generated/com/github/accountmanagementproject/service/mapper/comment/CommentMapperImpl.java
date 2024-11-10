package com.github.accountmanagementproject.service.mapper.comment;

import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.blog.Blog;
import com.github.accountmanagementproject.repository.blogComment.BlogComment;
import com.github.accountmanagementproject.web.dto.blog.CommentResponseDTO;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-10T18:03:13+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (JetBrains s.r.o.)"
)
public class CommentMapperImpl implements CommentMapper {

    @Override
    public CommentResponseDTO commentToCommentResponseDTO(BlogComment comment) {
        if ( comment == null ) {
            return null;
        }

        CommentResponseDTO commentResponseDTO = new CommentResponseDTO();

        commentResponseDTO.setUserName( commentUserNickname( comment ) );
        commentResponseDTO.setUserImg( commentUserProfileImg( comment ) );
        commentResponseDTO.setBlogId( commentBlogId( comment ) );
        commentResponseDTO.setCommentId( comment.getId() );
        commentResponseDTO.setContent( comment.getContent() );
        commentResponseDTO.setCreatedAt( comment.getCreatedAt() );

        return commentResponseDTO;
    }

    private String commentUserNickname(BlogComment blogComment) {
        if ( blogComment == null ) {
            return null;
        }
        MyUser user = blogComment.getUser();
        if ( user == null ) {
            return null;
        }
        String nickname = user.getNickname();
        if ( nickname == null ) {
            return null;
        }
        return nickname;
    }

    private String commentUserProfileImg(BlogComment blogComment) {
        if ( blogComment == null ) {
            return null;
        }
        MyUser user = blogComment.getUser();
        if ( user == null ) {
            return null;
        }
        String profileImg = user.getProfileImg();
        if ( profileImg == null ) {
            return null;
        }
        return profileImg;
    }

    private Integer commentBlogId(BlogComment blogComment) {
        if ( blogComment == null ) {
            return null;
        }
        Blog blog = blogComment.getBlog();
        if ( blog == null ) {
            return null;
        }
        Integer id = blog.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}

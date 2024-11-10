package com.github.accountmanagementproject.service.mapper.blog;

import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.blog.Blog;
import com.github.accountmanagementproject.repository.blogComment.BlogComment;
import com.github.accountmanagementproject.web.dto.blog.BlogResponseDTO;
import com.github.accountmanagementproject.web.dto.blog.BlogWithComment;
import com.github.accountmanagementproject.web.dto.blog.CommentResponseDTO;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-10T18:03:13+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (JetBrains s.r.o.)"
)
public class BlogMapperImpl implements BlogMapper {

    @Override
    public BlogResponseDTO blogToBlogResponseDTO(Blog blog) {
        if ( blog == null ) {
            return null;
        }

        BlogResponseDTO.BlogResponseDTOBuilder blogResponseDTO = BlogResponseDTO.builder();

        blogResponseDTO.userName( blogUserNickname( blog ) );
        blogResponseDTO.blogId( blog.getId() );
        blogResponseDTO.userImg( blogUserProfileImg( blog ) );
        blogResponseDTO.title( blog.getTitle() );
        blogResponseDTO.content( blog.getContent() );
        blogResponseDTO.record( blog.getRecord() );
        blogResponseDTO.distance( blog.getDistance() );
        blogResponseDTO.likeCount( blog.getLikeCount() );
        blogResponseDTO.createdAt( blog.getCreatedAt() );

        return blogResponseDTO.build();
    }

    @Override
    public CommentResponseDTO blogCommentToBlogCommentResponseDTO(BlogComment blogComment) {
        if ( blogComment == null ) {
            return null;
        }

        CommentResponseDTO commentResponseDTO = new CommentResponseDTO();

        commentResponseDTO.setContent( blogComment.getContent() );
        commentResponseDTO.setCreatedAt( blogComment.getCreatedAt() );

        return commentResponseDTO;
    }

    @Override
    public BlogWithComment blogToBlogWithCommentResponseDTO(Blog blog) {
        if ( blog == null ) {
            return null;
        }

        BlogWithComment.BlogWithCommentBuilder blogWithComment = BlogWithComment.builder();

        blogWithComment.userName( blogUserNickname( blog ) );
        blogWithComment.blogId( blog.getId() );
        blogWithComment.userImg( blogUserProfileImg( blog ) );
        blogWithComment.title( blog.getTitle() );
        blogWithComment.content( blog.getContent() );
        blogWithComment.record( blog.getRecord() );
        blogWithComment.distance( blog.getDistance() );
        blogWithComment.likeCount( blog.getLikeCount() );
        blogWithComment.createdAt( blog.getCreatedAt() );

        return blogWithComment.build();
    }

    private String blogUserNickname(Blog blog) {
        if ( blog == null ) {
            return null;
        }
        MyUser user = blog.getUser();
        if ( user == null ) {
            return null;
        }
        String nickname = user.getNickname();
        if ( nickname == null ) {
            return null;
        }
        return nickname;
    }

    private String blogUserProfileImg(Blog blog) {
        if ( blog == null ) {
            return null;
        }
        MyUser user = blog.getUser();
        if ( user == null ) {
            return null;
        }
        String profileImg = user.getProfileImg();
        if ( profileImg == null ) {
            return null;
        }
        return profileImg;
    }
}

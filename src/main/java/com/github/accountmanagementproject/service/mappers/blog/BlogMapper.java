package com.github.accountmanagementproject.service.mappers.blog;

import com.github.accountmanagementproject.repository.blog.Blog;
import com.github.accountmanagementproject.repository.blogComment.BlogComment;
import com.github.accountmanagementproject.web.dto.blog.BlogResponseDTO;
import com.github.accountmanagementproject.web.dto.blog.BlogWithComment;
import com.github.accountmanagementproject.web.dto.blog.CommentResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BlogMapper {
    BlogMapper INSTANCE = Mappers.getMapper(BlogMapper.class);

    @Mapping(source = "user.nickname", target = "userName")
    @Mapping(source = "id", target = "blogId")
    @Mapping(source = "user.profileImg", target = "userImg")
    BlogResponseDTO blogToBlogResponseDTO(Blog blog);

    CommentResponseDTO blogCommentToBlogCommentResponseDTO(BlogComment blogComment);

    @Mapping(source = "user.nickname", target = "userName")
    @Mapping(source = "id", target = "blogId")
    @Mapping(source = "user.profileImg", target = "userImg")
    BlogWithComment blogToBlogWithCommentResponseDTO(Blog blog);
}

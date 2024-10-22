package com.github.accountmanagementproject.service.mappers.blog;

import com.github.accountmanagementproject.repository.blog.Blog;
import com.github.accountmanagementproject.repository.blogComment.BlogComment;
import com.github.accountmanagementproject.web.dto.blog.BlogCommentResponseDTO;
import com.github.accountmanagementproject.web.dto.blog.BlogResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BlogMapper {
    BlogMapper INSTANCE = Mappers.getMapper(BlogMapper.class);

    @Mapping(source = "user.nickname", target = "nickname")
    BlogResponseDTO blogToBlogResponseDTO(Blog blog);

    BlogCommentResponseDTO blogCommentToBlogCommentResponseDTO(BlogComment blogComment);
}
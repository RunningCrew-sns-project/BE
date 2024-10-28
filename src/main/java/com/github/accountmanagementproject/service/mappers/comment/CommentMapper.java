package com.github.accountmanagementproject.service.mappers.comment;

import com.github.accountmanagementproject.repository.blogComment.BlogComment;
import com.github.accountmanagementproject.web.dto.blog.CommentResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(source = "user.nickname", target = "userName")
    @Mapping(source = "user.profileImg", target = "userImg")
    @Mapping(source = "blog.id", target = "blogId")
    @Mapping(source = "id", target = "commentId")
    CommentResponseDTO commentToCommentResponseDTO(BlogComment comment);
}

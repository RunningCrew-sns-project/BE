package com.github.runningcrewsnsproject.service.mapper.blog;

import com.github.runningcrewsnsproject.repository.blog.Blog;
import com.github.runningcrewsnsproject.repository.blogComment.BlogComment;
import com.github.runningcrewsnsproject.web.dto.blog.BlogResponseDTO;
import com.github.runningcrewsnsproject.web.dto.blog.BlogWithComment;
import com.github.runningcrewsnsproject.web.dto.blog.CommentResponseDTO;
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

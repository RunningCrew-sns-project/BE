package com.github.accountmanagementproject.service.mappers;

import com.github.accountmanagementproject.repository.blog.Blog;
import com.github.accountmanagementproject.web.dto.blog.BlogResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BlogMapper {
    BlogMapper INSTANCE = Mappers.getMapper(BlogMapper.class);

    @Mapping(source = "user.nickname", target = "nickname")
    BlogResponseDTO blogToBlogResponseDTO(Blog blog);

    @Mapping(source = "user.nickname", target = "nickname")
    List<BlogResponseDTO> blogsToBlogResponseDTOs(List<Blog> blogs);
}

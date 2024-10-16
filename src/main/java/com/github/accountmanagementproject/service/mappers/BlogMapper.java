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

    @Mapping(source = "image", target = "imageUrl")
    BlogResponseDTO blogToBlogResponseDTO(Blog blog);

    @Mapping(source = "image", target = "imageUrl")
    List<BlogResponseDTO> blogsToBlogResponseDTOs(List<Blog> blogs);
}

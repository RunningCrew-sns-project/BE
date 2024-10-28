package com.github.accountmanagementproject.service.mappers;

import com.github.accountmanagementproject.repository.blog.Blog;
import com.github.accountmanagementproject.web.dto.blog.BlogResponseDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-28T04:43:54+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.8.jar, environment: Java 17.0.11 (Oracle Corporation)"
)
public class BlogMapperImpl implements BlogMapper {

    @Override
    public BlogResponseDTO blogToBlogResponseDTO(Blog blog) {
        if ( blog == null ) {
            return null;
        }

        BlogResponseDTO.BlogResponseDTOBuilder blogResponseDTO = BlogResponseDTO.builder();

        blogResponseDTO.imageUrl( blog.getImage() );
        blogResponseDTO.id( blog.getId() );
        blogResponseDTO.title( blog.getTitle() );
        blogResponseDTO.content( blog.getContent() );
        blogResponseDTO.record( blog.getRecord() );

        return blogResponseDTO.build();
    }

    @Override
    public List<BlogResponseDTO> blogsToBlogResponseDTOs(List<Blog> blogs) {
        if ( blogs == null ) {
            return null;
        }

        List<BlogResponseDTO> list = new ArrayList<BlogResponseDTO>( blogs.size() );
        for ( Blog blog : blogs ) {
            list.add( blogToBlogResponseDTO( blog ) );
        }

        return list;
    }
}

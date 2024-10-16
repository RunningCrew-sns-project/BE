package com.github.accountmanagementproject.service.blog;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.repository.blog.Blog;
import com.github.accountmanagementproject.repository.blog.BlogRepository;
import com.github.accountmanagementproject.repository.blogComment.BlogCommentRepository;
import com.github.accountmanagementproject.repository.userLikesBlog.UserLikesBlog;
import com.github.accountmanagementproject.repository.userLikesBlog.UserLikesBlogRepository;
import com.github.accountmanagementproject.service.S3Service;
import com.github.accountmanagementproject.service.customExceptions.CustomNotFoundException;
import com.github.accountmanagementproject.service.mappers.BlogMapper;
import com.github.accountmanagementproject.web.dto.blog.BlogRequestDTO;
import com.github.accountmanagementproject.web.dto.blog.BlogResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class BlogService {
    private final BlogRepository blogRepository;
    private final BlogCommentRepository blogCommentRepository;
    private final UserLikesBlogRepository userLikesBlogRepository;
    private final AccountConfig accountConfig;
    private final S3Service s3Service;

    public String writeBlog(BlogRequestDTO blogRequestDTO, MyUser user, MultipartFile image) throws Exception {
        String imageUrl = s3Service.upload(image, "blog_images");

        Blog blog = Blog.builder()
                        .title(blogRequestDTO.getTitle())
                        .content(blogRequestDTO.getContent())
                        .record(blogRequestDTO.getRecord())
                        .user(user)
                        .build();

        if (image == null) {
            blog.setImage(null);
        } else {
            blog.setImage(imageUrl);
        }

        blogRepository.save(blog); //레포지토리에 저장
        return blog.getImage();
    }

    public void likeBlog(Integer blogId, MyUser user) {
        /*TODO : user와 blogId를 토대로 찾은 blog를 찾아 userLikesBlog 테이블에 존재하는지 확인
            1. 존재한다면 ? 좋아요 취소
            2. 존재하지 않는다면 ? 좋아요 누르기
        */

        Blog blog = blogRepository.findById(blogId).orElse(null);
        if (blog == null) {
            //TODO : notfound 예외 처리
            throw new CustomNotFoundException.ExceptionBuilder()
                    .customMessage("게시물이 없습니다.")
                    .request("존재하지 않는 게시물.")
                    .build();
        }
        UserLikesBlog userLikesBlog = userLikesBlogRepository.findByBlogAndUser(blog, user);
        if (userLikesBlog == null) { //존재하지 않으면 좋아요 테이블에 추가
            userLikesBlog = UserLikesBlog.builder()
                    .user(user)
                    .blog(blog)
                    .build();

            userLikesBlogRepository.save(userLikesBlog);
        }
        else { //존재하면 좋아요 테이블에서 삭제

        }

    }

    public List<BlogResponseDTO> getBlogs(MyUser user) {
        List<Blog> blogs = blogRepository.findAll();

        return BlogMapper.INSTANCE.blogsToBlogResponseDTOs(blogs);

    }
}

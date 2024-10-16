package com.github.accountmanagementproject.service.blog;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.repository.account.users.MyUsersJpa;
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
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
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
    private final MyUsersJpa myUsersJpa;
    private final EntityManager entityManager;

    @Transactional
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

    @Transactional
    public String likeBlog(Integer blogId, MyUser user) throws Exception {
        /*TODO : user와 blogId를 토대로 찾은 blog를 찾아 userLikesBlog 테이블에 존재하는지 확인
            1. 존재한다면 ? 좋아요 취소
            2. 존재하지 않는다면 ? 좋아요 누르기
        */

        try {
            Blog blog = blogRepository.findById(blogId).orElse(null);
            if (blog == null) {
                //TODO : notfound 예외 처리
                throw new CustomNotFoundException.ExceptionBuilder()
                        .customMessage("게시물이 없습니다.")
                        .request("존재하지 않는 게시물.")
                        .build();
            }
            List<Blog> blogs = user.getUserLikesBlogs().stream().map(UserLikesBlog::getBlog).toList();
            boolean isLiked = blogs.contains(blog);

            if(isLiked){
                //TODO : 좋아요 취소하면 갯수 감소
                blogRepository.decrementLikeCount(blog.getId());

                UserLikesBlog userLikesBlog = userLikesBlogRepository.findByUserAndBlog(user,blog);
                userLikesBlogRepository.delete(userLikesBlog);
                user.getUserLikesBlogs().remove(userLikesBlog);
            }else {
                //TODO: 좋아요 누르면 좋아요 갯수 증가
                blogRepository.incrementLikeCount(blog.getId());
                UserLikesBlog userLikesBlog = userLikesBlogRepository.save(UserLikesBlog.builder().user(user).blog(blog).build());
                user.getUserLikesBlogs().add(userLikesBlog);
            }

            return isLiked ? "좋아요를 취소했습니다." : "좋아요를 눌렀습니다.";

        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }

    public List<BlogResponseDTO> getBlogs(MyUser user) {
        List<Blog> blogs = blogRepository.findAll();

        return BlogMapper.INSTANCE.blogsToBlogResponseDTOs(blogs);

    }
}

package com.github.accountmanagementproject.service.blog;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.repository.account.users.MyUsersJpa;
import com.github.accountmanagementproject.repository.blog.Blog;
import com.github.accountmanagementproject.repository.blog.BlogRepository;
import com.github.accountmanagementproject.repository.blogComment.BlogCommentRepository;
import com.github.accountmanagementproject.repository.blogImages.BlogImages;
import com.github.accountmanagementproject.repository.blogImages.BlogImagesRepository;
import com.github.accountmanagementproject.repository.redis.RedisRepository;
import com.github.accountmanagementproject.repository.userLikesBlog.UserLikesBlog;
import com.github.accountmanagementproject.repository.userLikesBlog.UserLikesBlogRepository;
import com.github.accountmanagementproject.service.ExeTimer;
import com.github.accountmanagementproject.service.S3Service;
import com.github.accountmanagementproject.service.ScrollPaginationCollection;
import com.github.accountmanagementproject.service.customExceptions.CustomBadCredentialsException;
import com.github.accountmanagementproject.service.customExceptions.CustomNotFoundException;
import com.github.accountmanagementproject.service.mappers.blog.BlogMapper;
import com.github.accountmanagementproject.service.mappers.comment.CommentMapper;
import com.github.accountmanagementproject.web.dto.blog.BlogRequestDTO;
import com.github.accountmanagementproject.web.dto.blog.BlogResponseDTO;
import com.github.accountmanagementproject.web.dto.blog.BlogWithComment;
import com.github.accountmanagementproject.web.dto.blog.CommentResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
@Slf4j
public class BlogService {
    private final BlogRepository blogRepository;
    private final BlogCommentRepository blogCommentRepository;
    private final UserLikesBlogRepository userLikesBlogRepository;
    private final MyUsersJpa myUsersJpa;
    private final RedisRepository redisRepository;
    private final BlogImagesRepository blogImagesRepository;

    //https://kbwplace.tistory.com/178 No offset 방식 스크롤링 구현

//    @ExeTimer
//    public List<BlogResponseDTO> getAllBlogs(Integer size, MyUser user) {
//
//        List<UserLikesBlog> userLikesBlogs = userLikesBlogRepository.findByUser(user);
//
//        if(size == null || size <= 0)
//            size = 10;
//
//        Blog lastBlog = blogRepository.findTopByOrderByIdDesc();
//
//        if(lastBlog == null)
//            return Collections.emptyList();
//
//        Integer lastBlogId = lastBlog.getId();
//
//        PageRequest pageRequest = PageRequest.of(0, size);
//        Page<Blog> blogPage = blogRepository.findByIdLessThanOrderByIdDesc(lastBlogId + 1, pageRequest);
//        List<Blog> blogs = blogPage.getContent();
//
//        return blogs.stream()
//                .map(blog -> {
//                    BlogResponseDTO blogResponseDTO = BlogMapper.INSTANCE.blogToBlogResponseDTO(blog);
//                    blogResponseDTO.setLiked(userLikesBlogs.stream().map(UserLikesBlog::getBlog).toList().contains(blog));
//                    blogResponseDTO.setImageUrl(blogImagesRepository.findAllByBlog(blog).stream().map(BlogImages::getImageUrl).toList());
//                    return blogResponseDTO;
//                })
//                .toList();
//    }

    //https://kbwplace.tistory.com/178 No offset 방식 스크롤링 구현
    public ScrollPaginationCollection<BlogResponseDTO> getAllBlogs(Integer size, Integer cursor, MyUser user) {
        List<UserLikesBlog> userLikesBlogs = userLikesBlogRepository.findByUser(user);

        if (size == null || size <= 0)
            size = 10;

        Integer lastBlogId;
        if (cursor != null) {
            lastBlogId = cursor;
        } else {
            Blog topBlog = blogRepository.findTopByOrderByIdDesc();
            lastBlogId = topBlog != null ? topBlog.getId() : 0; // 블로그가 없을 경우 0
        }

        PageRequest pageRequest = PageRequest.of(0, size + 1);
        Page<Blog> blogPage = blogRepository.findByIdLessThanOrderByIdDesc(lastBlogId + 1, pageRequest);
        List<Blog> blogs = blogPage.getContent();

        List<BlogResponseDTO> responseItems = blogs.stream()
                .map(blog -> {
                    BlogResponseDTO blogResponseDTO = BlogMapper.INSTANCE.blogToBlogResponseDTO(blog);
                    blogResponseDTO.setLiked(userLikesBlogs.stream().map(UserLikesBlog::getBlog).toList().contains(blog));
                    blogResponseDTO.setImageUrl(blogImagesRepository.findAllByBlog(blog).stream().map(BlogImages::getImageUrl).toList());
                    return blogResponseDTO;
                })
                .toList();

        boolean lastScroll = responseItems.size() <= size;
        List<BlogResponseDTO> currentScrollItems = lastScroll ? responseItems : responseItems.subList(0, size);
        BlogResponseDTO nextCursor = lastScroll ? null : responseItems.get(size);

        return ScrollPaginationCollection.of(currentScrollItems, size, lastScroll, nextCursor);
    }

    @ExeTimer
    public BlogWithComment getBlogById(Integer blogId, MyUser user) {
        // 블로그에 대한 댓글 가져오기
        Blog blog = blogRepository.findById(blogId).orElse(null);
        List<String> imageUrlList = blogImagesRepository.findAllByBlog(blog)
                .stream()
                .map(BlogImages::getImageUrl)
                .toList();

        List<CommentResponseDTO> comments = blogCommentRepository.findAllByBlog(blog).
                stream().map(CommentMapper.INSTANCE::commentToCommentResponseDTO)
                .toList();

        BlogWithComment blogWithComment = BlogMapper.INSTANCE.blogToBlogWithCommentResponseDTO(blog);
        blogWithComment.setComments(comments);
        blogWithComment.setLiked(userLikesBlogRepository.findByUserAndBlog(user, blog) != null);
        blogWithComment.setImageUrl(imageUrlList);

        return blogWithComment;
    }
    @ExeTimer
    @Transactional
    public BlogResponseDTO writeBlog(BlogRequestDTO blogRequestDTO, MyUser user) throws Exception {
        List<BlogImages> blogImagesList = new ArrayList<>();


        Blog blog = Blog.builder()
                .title(blogRequestDTO.getTitle())
                .content(blogRequestDTO.getContent())
                .record(blogRequestDTO.getRecord())
                .distance(blogRequestDTO.getDistance())
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        blogRepository.save(blog); //레포지토리에 저장

        if(blogRequestDTO.getImageUrl() == null || blogRequestDTO.getImageUrl().isEmpty())
            blogImagesList.add(
                    BlogImages.builder()
                    .blog(blog)
                    .imageUrl("https://running-crew.s3.ap-northeast-2.amazonaws.com/default_image/blog_default.jpg")
                    .build()
            );
        else {
            for(String imageUrl : blogRequestDTO.getImageUrl()){
                BlogImages blogImage = BlogImages.builder()
                        .blog(blog)
                        .imageUrl(imageUrl)
                        .build();
                blogImagesList.add(blogImage);
            }
        }

        blogImagesRepository.saveAll(blogImagesList);

        return BlogMapper.INSTANCE.blogToBlogResponseDTO(blog);
    }
    @ExeTimer
    @Transactional
    @Async
    //비동기적으로 처리 ? redis에 저장해놨다가 나중에 가져와서 db에 저장?
    public CompletableFuture<String> likeBlog(Integer blogId, MyUser user) throws Exception {

        // 좋아요 누르면 redis에 blog_00:11로 저장
        // redis에 저장해놨다가 비동기적으로 나중에 db에 저장

        Blog blog = blogRepository.findById(blogId).orElse(null);
        if (blog == null) {
            throw new CustomNotFoundException.ExceptionBuilder()
                    .customMessage("게시물이 없습니다.")
                    .request("존재하지 않는 게시물.")
                    .build();
        }

        String redisKey = "blog_" + blogId + ":" + user.getUserId();
        boolean isLiked = redisRepository.getValue(redisKey) != null;

        if (isLiked) {
            // 좋아요 취소
            redisRepository.getAndDeleteValue(redisKey);
            return CompletableFuture.completedFuture("좋아요를 취소했습니다.");
        } else {
            // 좋아요 누르기
            redisRepository.save(redisKey, String.valueOf(blog.getLikeCount()), Duration.ofMinutes(10));
            return CompletableFuture.completedFuture("좋아요를 눌렀습니다.");
        }
    }

    @ExeTimer
    @Transactional
//    @Scheduled(fixedDelay = 30000) //비동기 타이머 10초마다
    protected void syncUserLikesBlog(){
        Set<String> keys = redisRepository.keys("blog_*"); //레디스에서 blog_ 패턴에 해당 하는 키 모두 가져오기 (중복 없이)

        for (String key : keys) { //반복문 돌며
            log.info(key);
            String[] parts = key.split(":"); //blog_00:11 을 : 기준으로 분리
            Integer blogId = Integer.parseInt(parts[0].split("_")[1]); //blog_00 에서 00만 가져오기
            Integer userId = Integer.parseInt(parts[1]); // blog_00:11 에서 11만 가져오기

            Blog blog = blogRepository.findById(blogId).orElse(null); //해당하는 블로그 가져오기
            MyUser user = myUsersJpa.findById(userId).orElse(null); //해당하는 유저 가져오기

            if (blog != null && user != null) {
                UserLikesBlog userLikesBlog = userLikesBlogRepository.findByUserAndBlog(user, blog);
                if(userLikesBlog == null) { //db에 없을때 새로 저장 (좋아요 누르기)
                    UserLikesBlog newUserLikesBlog = UserLikesBlog.builder()
                            .blog(blog)
                            .user(user)
                            .build();
                    userLikesBlogRepository.save(newUserLikesBlog);
                }
                else {
                    userLikesBlogRepository.delete(userLikesBlog); //db에 있으면 삭제 (좋아요 취소)
                }
                Integer likeCount = userLikesBlogRepository.countAllByBlog(blog); //db에서 blog에 해당하는 좋아요 갯수 가져오기
                blog.setLikeCount(likeCount); //좋아요 갯수 저장
                blogRepository.save(blog); //좋아요 갯수 저장
            }

            redisRepository.getAndDeleteValue(key); //레디스 데이터 삭제
        }
    }


    @ExeTimer
    @Transactional
    public String updateBlog(BlogRequestDTO blogRequestDTO,Integer blogId, MyUser user) throws IOException {
        Blog blog = blogRepository.findById(blogId).orElse(null);

        blogImagesRepository.deleteAllByBlog(blog);

        if(!blog.getUser().equals(user)){
            throw new CustomBadCredentialsException.ExceptionBuilder()
                    .customMessage("권한이 없습니다.")
                    .request("작성자의 권한이 필요합니다.")
                    .build();
        }
        blog.setTitle(blogRequestDTO.getTitle());
        blog.setContent(blogRequestDTO.getContent());
        blog.setRecord(blogRequestDTO.getRecord());
        blog.setDistance(blogRequestDTO.getDistance());

        blogRepository.save(blog);

        for(String imageUrl : blogRequestDTO.getImageUrl()){
            blogImagesRepository.save(BlogImages.builder()
                    .blog(blog)
                    .imageUrl(imageUrl)
                    .build());
        }

        return "성공적으로 수정하였습니다.";
    }

    @ExeTimer
    @Transactional
    public String deleteBlog(Integer blogId, MyUser user) {
        Blog blog = blogRepository.findById(blogId).orElse(null);
        if(!blog.getUser().equals(user)){
            throw new CustomBadCredentialsException.ExceptionBuilder()
                    .customMessage("권한이 없습니다.")
                    .request("작성자의 권한이 필요합니다.")
                    .build();
        }
        //TODO:

        blogRepository.delete(blog);

        return "성공적으로 삭제하였습니다.";
    }

}

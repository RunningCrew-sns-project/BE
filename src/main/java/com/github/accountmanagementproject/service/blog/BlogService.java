package com.github.accountmanagementproject.service.blog;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.exception.CustomBadCredentialsException;
import com.github.accountmanagementproject.exception.CustomNotFoundException;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.account.user.MyUsersRepository;
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
import com.github.accountmanagementproject.service.mapper.blog.BlogMapper;
import com.github.accountmanagementproject.service.mapper.comment.CommentMapper;
import com.github.accountmanagementproject.web.dto.blog.BlogRequestDTO;
import com.github.accountmanagementproject.web.dto.blog.BlogResponseDTO;
import com.github.accountmanagementproject.web.dto.blog.BlogWithComment;
import com.github.accountmanagementproject.web.dto.blog.CommentResponseDTO;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final MyUsersRepository myUsersJpa;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisRepository redisRepository;
    private final BlogImagesRepository blogImagesRepository;

    private HashOperations<String, Object, Object> hashOperations;

    @PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
    }

    //https://kbwplace.tistory.com/178 No offset 방식 스크롤링 구현
    //https://velog.io/@dbsxogh96/Redis%EB%A1%9C-%EC%A1%B0%ED%9A%8C%EC%88%98-%EC%A2%8B%EC%95%84%EC%9A%94-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0
    //TODO : 좋아요 갯수 redis에 저장 하는 로직 구현
    public ScrollPaginationCollection<BlogResponseDTO> getAllBlogs(Integer size, Integer cursor, MyUser user, Boolean isMyBlog) {
        List<UserLikesBlog> userLikesBlogs = userLikesBlogRepository.findByUser(user);

        Integer lastBlogId = null;

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
                .filter(blog -> !isMyBlog || blog.getUser().equals(user))
                .map(blog -> {
                    //TODO: blog의 likeCount 가져와서 redis에 저장 후 좋아요 누르면 증감 구현
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

        for(String imageUrl : blogRequestDTO.getImageUrl()){
            BlogImages blogImage = BlogImages.builder()
                    .blog(blog)
                    .imageUrl(imageUrl)
                    .build();
            blogImagesList.add(blogImage);
        }
        blogImagesRepository.saveAll(blogImagesList);

        return BlogMapper.INSTANCE.blogToBlogResponseDTO(blog);
    }


    @ExeTimer
    @Transactional
    @Async
    //비동기적으로 처리 ? redis에 저장해놨다가 나중에 가져와서 db에 저장?
    public CompletableFuture<String> likeBlog(Integer blogId, MyUser user) throws Exception {

        // 좋아요 누르면 redis에 blog_00:11(유저 아이디)로 저장
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
            redisRepository.save(redisKey, String.valueOf(blog.getLikeCount()), Duration.ofMinutes(1));
            return CompletableFuture.completedFuture("좋아요를 눌렀습니다.");
        }
    }

    @ExeTimer
    @Transactional
    @Scheduled(fixedDelay = 30000) //비동기 타이머 30초마다
    protected void syncUserLikesBlog(){
        Set<String> keys = redisRepository.keys("blog_*"); //레디스에서 blog_ 패턴에 해당 하는 키 모두 가져오기 (중복 없이)
        ScanOptions scanOptions = ScanOptions.scanOptions().match("blog_*").count(100).build();
        Cursor<byte[]> keys_scan = redisTemplate.getConnectionFactory().getConnection().scan(scanOptions);


        https://velog.io/@sejinkim/Redis-KEYS-vs-SCAN
//        여기 한번 보고 수정

        for (String key : keys) { //반복문 돌며
            log.info(key);
            String[] parts = key.split(":"); //blog_00:11 을 : 기준으로 분리
            Integer blogId = Integer.parseInt(parts[0].split("_")[1]); //blog_00 에서 00만 가져오기
            Integer userId = Integer.parseInt(parts[1]); // blog_00:11 에서 11만 가져오기

            Blog blog = blogRepository.findById(blogId).orElseThrow(()-> new CustomNotFoundException.ExceptionBuilder().customMessage("해당 블로그를 찾을 수 없습니다.").build()); //해당하는 블로그 가져오기
            MyUser user = myUsersJpa.findById(userId).orElseThrow(()->new CustomNotFoundException.ExceptionBuilder().customMessage("해당 유저를 찾을 수 없습니다.").build()); //해당하는 유저 가져오기

            UserLikesBlog userLikesBlog = userLikesBlogRepository.findByUserAndBlog(user, blog);
            if(userLikesBlog == null) { //db에 없을때 새로 저장 (좋아요 누르기)

                //UserLikeBlog에 isLiked 변수에 기본값으로 true 저장
                UserLikesBlog newUserLikesBlog = UserLikesBlog.builder()
                        .blog(blog)
                        .user(user)
                        .build();
                userLikesBlogRepository.save(newUserLikesBlog);
            }
            else {
                userLikesBlog.setIsLiked(false);
            }
            Integer likeCount = userLikesBlogRepository.countAllByBlog(blog); //db에서 blog에 해당하는 좋아요 갯수 가져오기
            blog.setLikeCount(likeCount); //좋아요 갯수 저장

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

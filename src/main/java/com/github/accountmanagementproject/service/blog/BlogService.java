package com.github.accountmanagementproject.service.blog;

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
import com.github.accountmanagementproject.service.ScrollPaginationCollection;
import com.github.accountmanagementproject.service.mapper.blog.BlogMapper;
import com.github.accountmanagementproject.web.dto.blog.BlogDetails;
import com.github.accountmanagementproject.web.dto.blog.BlogRequestDTO;
import com.github.accountmanagementproject.web.dto.blog.BlogResponseDTO;
import jakarta.annotation.PreDestroy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
@Service
@Slf4j
@EnableScheduling
public class BlogService {
    private final BlogRepository blogRepository;
    private final UserLikesBlogRepository userLikesBlogRepository;
    private final MyUsersRepository myUsersJpa;
    private final RedisRepository redisRepository;
    private final BlogImagesRepository blogImagesRepository;
    private final RedisHashService redisHashService;


    //https://kbwplace.tistory.com/178 No offset 방식 스크롤링 구현
    //https://velog.io/@dbsxogh96/Redis%EB%A1%9C-%EC%A1%B0%ED%9A%8C%EC%88%98-%EC%A2%8B%EC%95%84%EC%9A%94-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0
    //TODO : 좋아요 갯수 redis에 저장 하는 로직 구현
    @ExeTimer
    public ScrollPaginationCollection<BlogResponseDTO> getAllBlogs(Integer size, Integer cursor, MyUser user, Boolean isMyBlog) {
        //TODO : 메소드 실행할때마다 레포지토리에 접근하게 되니까 개선 필요

        //cursor가 다음 불러올 리스트의 첫번째 DTO를 가리키고 있는데, 처음 조회하면 null 이기 때문에 역순에서 가장 첫번째(가장 마지막)를 lastBlogId 값으로,
        //cursor가 다음 불러올 리스트의 첫번째 DTO를 가리키고 있다면 cursor를 lastBlogId로 대입
        Integer lastBlogId = (cursor != null) ? cursor : blogRepository.findTopByOrderByIdDesc().getId();

        //PageRequest 의 생성자 : PageRequest (페이지 번호, 가져올 사이즈)
        //https://velog.io/@conatuseus/JPA-Paging-%ED%8E%98%EC%9D%B4%EC%A7%80-%EB%82%98%EB%88%84%EA%B8%B0-o7jze1wqhj
        PageRequest pageRequest = PageRequest.of(0, size + 1);
        // 요청한 size 보다 한개 더, 왜냐하면 다음 리스트의 첫번째 요소를 cursor로 가리켜야 하기 때문에 //size가 10이면 11개 가져오도록

        Page<Blog> blogPage = isMyBlog ? //내가 작성한 블로그를 조회하는지 확인
                //마지막부터 DESC 로 정렬해서 lastBlogId(다음 목록의 첫번째) 전까지 pageReqeust 만큼 가져와서
                blogRepository.findByUserAndIdLessThanOrderByIdDesc(user, lastBlogId + 1, pageRequest) :
                blogRepository.findByIdLessThanOrderByIdDesc(lastBlogId + 1, pageRequest);

        List<Blog> blogs = blogPage.getContent();
        List<BlogResponseDTO> responseItems = mappingBlogResponse(user, blogs);

        boolean lastScroll = responseItems.size() <= size; // 요청한 사이즈 보다 불러온 사이즈가 작으면? 마지막이라는 얘기니까 lastScroll = true
        List<BlogResponseDTO> currentScrollItems = lastScroll ? responseItems : responseItems.subList(0, size);
        BlogResponseDTO nextCursor = lastScroll ? null : responseItems.get(size); //ex) 0부터 시작하면 9까지 10개이고, size는 10이니까 11번째꺼 가져오기

        return ScrollPaginationCollection.of(currentScrollItems, size, lastScroll, nextCursor);
    }

    private List<BlogResponseDTO> mappingBlogResponse(MyUser user, List<Blog> blogs){
        //좋아요 목록을 불러와서 저장
        Set<Integer> userLikesBlogsIds = getUserLikesBlogsIds(user, blogs);

        return blogs.stream()
                .map(blog -> {
                    //TODO: blog의 likeCount 가져와서 redis에 저장 후 좋아요 누르면 증감 구현
                    BlogResponseDTO blogResponseDTO = BlogMapper.INSTANCE.blogToBlogResponseDTO(blog);
                    //set에 블로그가 존재하면 이미 좋아요 누른거, 없으면 안누른거
                    blogResponseDTO.setLiked(userLikesBlogsIds.contains(blog.getId()));
                    blogResponseDTO.setImageUrl(blogImagesRepository.findAllByBlog(blog).stream().map(BlogImages::getImageUrl).toList());
                    //레디스에 blog_아이디 를 해시키, likeCount라는 필드에, likeCount를 value (hashkey, filed, value) 3가지 조합 패턴으로 구성
                    //exists) blog_01, likeCount, 10
                    redisHashService.save("blog_" + blog.getId(), "likeCount", blog.getLikeCount().toString());

                    return blogResponseDTO;
                })
                .toList();
    }

    //레포지토리에서 레디스에
    // user_likes:user_id , blog_id, true/false  -> 형태로 저장
    //TODO : 가져온 블로그에서 좋아요 눌렀는지 검사해야함. 모든 좋아요 검사하니까 에러남 -> 불러온 목록에서만 좋아요 조회
    private Set<Integer> getUserLikesBlogsIds(MyUser user, List<Blog> blogs){
        //좋아요 정보가 존재하지 않으면 db에서 가져와서 레디스에 좋아요 정보 저장하고
        Set<Integer> userLikesBlogsIds = new HashSet<>();

        for (Blog blog : blogs) {
            UserLikesBlog userLikesBlog = userLikesBlogRepository.findByUserAndBlog(user, blog);
            if (userLikesBlog != null) {
                boolean isLiked = userLikesBlog.getIsLiked();

                if (isLiked) {
                    // 좋아요가 눌린 게시물인 경우
                    redisHashService.save("user_likes:" + user.getUserId(), userLikesBlog.getBlog().getId().toString(), "true");
                    userLikesBlogsIds.add(userLikesBlog.getBlog().getId());
                } else {
                    // 좋아요가 눌리지 않은 게시물인 경우
                    redisHashService.save("user_likes:" + user.getUserId(), userLikesBlog.getBlog().getId().toString(), "false");
                }
            }
        }

        //좋아요 누른 게시물 아이디 Set으로  return
        return userLikesBlogsIds;
    }

    @ExeTimer
    public BlogDetails getBlogById(Integer blogId, MyUser user) {
        // 블로그에 대한 댓글 가져오기
        Blog blog = blogRepository.findById(blogId).orElseThrow(()->new CustomNotFoundException.ExceptionBuilder().customMessage("블로그를 찾을 수 없습니다.").build());

        List<String> imageUrlList = blogImagesRepository.findAllByBlog(blog)
                .stream()
                .map(BlogImages::getImageUrl)
                .toList();

        BlogDetails blogDetails = BlogMapper.INSTANCE.blogToBlogDetailsDTO(blog);
        blogDetails.setLiked(userLikesBlogRepository.findByUserAndBlog(user, blog) != null && userLikesBlogRepository.findByUserAndBlog(user, blog).getIsLiked());
        blogDetails.setImageUrl(imageUrlList);

        return blogDetails;
    }
    @ExeTimer
    @Transactional
    public BlogResponseDTO writeBlog(BlogRequestDTO blogRequestDTO, MyUser user){
        Blog blog = Blog.builder()
                .title(blogRequestDTO.getTitle())
                .content(blogRequestDTO.getContent())
                .record(blogRequestDTO.getRecord())
                .distance(blogRequestDTO.getDistance())
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        blogRepository.save(blog); //레포지토리에 저장

        //블로그 이미지 저장
        saveBlogImages(blogRequestDTO.getImageUrl(), blog);

        return BlogMapper.INSTANCE.blogToBlogResponseDTO(blog);
    }

    private void saveBlogImages(List<String> imageUrls, Blog blog){
        List<BlogImages> blogImagesList = new ArrayList<>();
        for(String imageUrl : imageUrls){
            BlogImages blogImage = BlogImages.builder()
                    .blog(blog)
                    .imageUrl(imageUrl)
                    .build();
            blogImagesList.add(blogImage);
        }
        blogImagesRepository.saveAll(blogImagesList);
    }


    @ExeTimer
    @Transactional
//    @Async
    //비동기적으로 처리 ? redis에 저장해놨다가 나중에 가져와서 db에 저장?
    public String likeBlog(Integer blogId, MyUser user){
        // redis에 저장해놨다가 비동기적으로 나중에 db에 저장

        Blog blog = blogRepository.findById(blogId).orElseThrow(() -> new CustomNotFoundException.ExceptionBuilder()
                .customMessage("블로그를 찾을 수 없습니다.")
                .build());

        String hashKey = "user_likes:" + user.getUserId();
        String key = blog.getId().toString();
        String likeStatus = redisHashService.get(hashKey, key);

        boolean isLiked = "true".equals(likeStatus); //likeStatus가 true이면 isLiked에 true, null이거나 true가 아니면 false

        //TODO : 블로그 조회 시 좋아요 정보를 미리 가지고 와서 처리
        if(isLiked){
            redisHashService.save(hashKey, key, "false");
            redisHashService.decrement("blog_" + key, "likeCount");
            syncUserLikesBlog();
            return "좋아요를 취소했습니다.";
        }else {
            redisHashService.save(hashKey, key, "true");
            redisHashService.increment("blog_" + key, "likeCount");
            syncUserLikesBlog();
            return "좋아요를 눌렀습니다.";
        }
    }


    @ExeTimer
    @Transactional
//    @Async
//    @Scheduled(fixedDelay = 5000) //비동기 타이머 1초마다
    protected void syncUserLikesBlog(){
        Set<String> keys = redisRepository.keys("user_likes:*");
        log.info(keys.toString());

//        https://velog.io/@sejinkim/Redis-KEYS-vs-SCAN
//        여기 한번 보고 수정
        //TODO : getUserLikesBlogsIds 참고하여 db에 저장
        if(!keys.isEmpty()){
            for (String key : keys) { //반복문 돌며

                Integer userId = Integer.valueOf(key.substring(11) );
                MyUser user = myUsersJpa.findById(userId).orElseThrow(()->new CustomNotFoundException.ExceptionBuilder().customMessage("해당 유저를 찾을 수 없습니다.").build()); //해당하는 유저 가져오기

                Map<String, String> resultMap = redisHashService.getAll(key);


                for(String blogId : resultMap.keySet()){
                    Boolean isLiked = Boolean.parseBoolean(resultMap.get(blogId));

                    Blog blog = blogRepository.findById(Integer.valueOf(blogId)).orElseThrow(()-> new CustomNotFoundException.ExceptionBuilder().customMessage("해당 블로그를 찾을 수 없습니다.").build()); //해당하는 블로그 가져오기

                    UserLikesBlog userLikesBlog = userLikesBlogRepository.findByUserAndBlog(user, blog);

                    if(userLikesBlog == null) {
                        userLikesBlog = UserLikesBlog.builder()
                                .blog(blog)
                                .user(user)
                                .isLiked(isLiked)
                                .build();

                        userLikesBlogRepository.save(userLikesBlog);
                    }else {
                        userLikesBlog.setIsLiked(isLiked);
                        userLikesBlogRepository.save(userLikesBlog);
                    }

                    //TODO : 여기서 레디스 데이터 가져올때
                    // java.lang.NumberFormatException: Cannot parse null string
                    //	at java.base/java.lang.Integer.parseInt(Integer.java:550) ~[na:na]
                    //	at java.base/java.lang.Integer.valueOf(Integer.java:935) ~[na:na]
                    // 에러가 나는데 레디스에서 데이터 가져올때 뭔가 문제가 있는 듯 함
                    Integer likeCount = Integer.valueOf(redisHashService.get("blog_" + blogId, "likeCount")); //db에서 blog에 해당하는 좋아요 갯수 가져오기
                    blog.setLikeCount(likeCount); //좋아요 갯수 저장
                }
            }
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
        blog.setCreatedAt(LocalDateTime.now());

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

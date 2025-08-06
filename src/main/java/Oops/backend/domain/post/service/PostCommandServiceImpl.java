package Oops.backend.domain.post.service;


import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.lesson.service.LessonCommandService;
import Oops.backend.domain.category.entity.Category;
import Oops.backend.domain.category.repository.CategoryRepository;
import Oops.backend.domain.post.dto.PostCreateRequest;
import Oops.backend.domain.post.dto.PostCreateResponse;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.model.Situation;
import Oops.backend.domain.post.repository.PostRepository;
import Oops.backend.domain.postGroup.entity.PostGroup;
import Oops.backend.config.s3.S3ImageService;

import Oops.backend.domain.postGroup.repository.PostGroupRepository;
import Oops.backend.domain.randomTopic.Repository.RandomTopicRepository;
import Oops.backend.domain.randomTopic.entity.RandomTopic;
import Oops.backend.domain.user.entity.User;
import Oops.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostCommandServiceImpl implements PostCommandService{

    private final PostRepository postRepository;
    private final PostLikeQueryService postLikeQueryService;
    private final PostLikeCommandService postLikeCommandService;
    private final LessonCommandService lessonCommandService;
    private final CategoryRepository categoryRepository;
    private final RandomTopicRepository randomTopicRepository;
    private final PostGroupRepository postGroupRepository;
    private final UserRepository userRepository;
    private final S3ImageService s3ImageService;

    @Override
    @Transactional
    public void likePost(Long postId, User user) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NO_POST));

        if (!postLikeQueryService.findPostLike(user, post)){
            postRepository.plusPostLikes(postId);
            postLikeCommandService.createPostLike(post, user);
        }
        else{
            try {
                postRepository.minusPostLikes(postId);
                postLikeCommandService.deletePostLike(post, user);
            } catch(DataIntegrityViolationException e){
                throw new GeneralException(ErrorStatus.ALREADY_LIKED_POST);
            }

        }
    }

    @Override
    @Transactional
    public void deletePost(Long postId, User user) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NO_POST));

        log.info("post.getUser().equals(user)={}", post.getUser().equals(user));

        // TODO : 검증 로직
        // 사용자가 게시글을 작성한 사용자와 일치하지 않을 경우
        if ((post.getUser().getId()) != user.getId()){
            throw new GeneralException(ErrorStatus.UNAUTHORIZED_FOR_POST);
        }

        //게시글에 대한 모든 Lesson도 제거
        lessonCommandService.deleteAllLessonsOfPost(post);

        postRepository.delete(post);

        // TODO : 극복 중이 존재할 때 웁스 중 삭제할 수 있는지

        // TODO : 게시글 삭제 후 PostGroup이 null이면 PostGroup도 삭제하기
    }

    @Override
    public void watchPost(Long postId) {
        postRepository.plusPostWatching(postId);
    }

    //실패담 작성
    @Override
    @Transactional
    public PostCreateResponse createPost(User user, PostCreateRequest request,List<MultipartFile> imageFiles) {

        // 상황 필수
        Situation situation = request.getSituation();
        if (situation == null) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST, "상황 정보가 누락되었습니다.");
        }

        // 1. 카테고리 조회 (optional)
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND, "존재하지 않는 카테고리입니다."));
        }

        // 2. 랜덤 주제 조회 (optional)
        RandomTopic topic = null;
        if (request.getTopicId() != null) {
            topic = randomTopicRepository.findById(request.getTopicId())
                    .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND, "존재하지 않는 랜덤 주제입니다."));
        }

        // 3. 둘 다 null이면 예외
        if (category == null && topic == null) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST, "카테고리 또는 랜덤 주제 중 하나는 반드시 선택해야 합니다.");
        }

        // 4. Post 생성
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setSituation(request.getSituation());
        post.setCategory(category);
        post.setTopic(topic);
        post.setUser(user);
        //post.setImages(request.getImageUrls());

        post.setLikes(0);
        post.setWatching(0);
        post.setReportCnt(0);
        post.setWantedCommentTypes(
                request.getWantedCommentTypes() == null
                        ? Collections.emptyList()
                        : request.getWantedCommentTypes());

        // PostGroup 처리
        PostGroup postGroup = null;
        if (situation == Situation.OOPS) {
            if (category == null && topic == null) {
                throw new GeneralException(ErrorStatus._BAD_REQUEST, "OOPS 상황에서는 categoryId 또는 topicId 중 하나는 필수입니다.");
            }

            postGroup = new PostGroup();
            postGroup.setCategory(category);
            postGroup = postGroupRepository.save(postGroup); // 저장 필수
            post.setPostGroup(postGroup);

        }else if (situation == Situation.OVERCOMING || situation == Situation.OVERCOME) {
            if (request.getPreviousPostId() == null) {
                throw new GeneralException(ErrorStatus._BAD_REQUEST, "이 상황에서는 이전 게시글 ID가 필요합니다.");
            }

            Post previousPost = postRepository.findById(request.getPreviousPostId())
                    .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND, "이전 게시글을 찾을 수 없습니다."));

            PostGroup group = previousPost.getPostGroup();
            if (group == null) {
                throw new GeneralException(ErrorStatus._NOT_FOUND, "이전 게시글에 PostGroup이 없습니다.");
            }

            post.setPostGroup(group);
        }

        user.addPoint(10);
        userRepository.save(user);

        Post saved = postRepository.save(post);

        //postId 값으로 파일 이름을 설정하기 위해 Post 저장 후 이미지 저장
        List<String> uploadedImageUrls = new ArrayList<>();

        if (imageFiles != null && !imageFiles.isEmpty()) {
            uploadedImageUrls = imageFiles.stream()
                    .map((imageFile) -> s3ImageService.upload(imageFile, "posts", saved.getId().toString()))
                    .toList();
        }
        post.setImages(uploadedImageUrls);

        return PostCreateResponse.builder()
                .postId(saved.getId())
                .message("실패담 작성 완료! 10포인트가 적립되었습니다.")
                .imageUrls(uploadedImageUrls)
                .build();
    }



}

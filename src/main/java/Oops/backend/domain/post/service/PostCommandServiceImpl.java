package Oops.backend.domain.post.service;


import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.category.entity.Category;
import Oops.backend.domain.category.repository.CategoryRepository;
import Oops.backend.domain.post.dto.PostCreateRequest;
import Oops.backend.domain.post.dto.PostCreateResponse;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.repository.PostRepository;
import Oops.backend.domain.randomTopic.Repository.RandomTopicRepository;
import Oops.backend.domain.randomTopic.entity.RandomTopic;
import Oops.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class PostCommandServiceImpl implements PostCommandService{

    private final PostRepository postRepository;
    private final PostLikeQueryService postLikeQueryService;
    private final PostLikeCommandService postLikeCommandService;
    private final CategoryRepository categoryRepository;
    private final RandomTopicRepository randomTopicRepository;

    @Override
    @Transactional
    public void cheerPost(Long postId, User user) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST, "존재하지 않는 게시글입니다."));

        if (!postLikeQueryService.findPostLike(user, post)){
            post.plusCheer();
            postLikeCommandService.createPostLike(post, user);
        }
        else{
            post.minusCheer();
            postLikeCommandService.deletePostLike(post, user);
        }
    }

    //실패담 작성
    @Override
    @Transactional
    public PostCreateResponse createPost(User user, PostCreateRequest request) {

        // 1. 카테고리 조회
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        // 2. 랜덤 주제 조회
        RandomTopic randomTopic = null;
        if (request.getTopicId() != null) {
            randomTopic = randomTopicRepository.findById(request.getTopicId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주제입니다."));
        }

        // 3. Post 생성 및 저장
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setSituation(request.getSituation());
        post.setCategory(category);
        post.setTopic(randomTopic);
        post.setUser(user);
        post.setImages(request.getImageUrls());
        post.setLikes(0);
        post.setWatching(0);
        post.setReportCnt(0);
        post.setComments(Collections.emptyList());
        post.setPostGroup(null); // 현재 단독 작성으로 처리

        Post savedPost = postRepository.save(post);

        // 4. 응답 반환
        return PostCreateResponse.builder()
                .postId(savedPost.getId())
                .message("실패담 작성 완료")
                .build();
    }
}

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

        // categoryId도 없고 topicId도 없으면 예외
        //if (request.getCategoryId() == null && request.getTopicId() == null) {
        //    throw new IllegalArgumentException("카테고리 또는 랜덤 주제 중 하나는 반드시 선택해야 합니다.");
        //}

        // 선택적 category 조회
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
        }

        // 선택적 topic 조회
        //RandomTopic topic = null;
        //if (request.getTopicId() != null) {
        //    topic = randomTopicRepository.findById(request.getTopicId())
        //            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 랜덤 주제입니다."));
        //}

        // Post 생성
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setSituation(request.getSituation());
        post.setCategory(category);
        //post.setTopic(topic);
        post.setUser(user);
        post.setImages(request.getImageUrls());
        post.setLikes(0);
        post.setWatching(0);
        post.setReportCnt(0);
        post.setComments(Collections.emptyList());
        post.setPostGroup(null);

        Post saved = postRepository.save(post);

        return PostCreateResponse.builder()
                .postId(saved.getId())
                .message("실패담 작성 완료")
                .build();
    }

}

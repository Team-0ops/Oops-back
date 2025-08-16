package Oops.backend.domain.postGroup.service;


import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.config.s3.S3ImageService;
import Oops.backend.domain.category.dto.CategoryResponse;
import Oops.backend.domain.comment.dto.CommentResponse;
import Oops.backend.domain.comment.service.CommentLikeService;
import Oops.backend.domain.post.dto.PostResponse;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.model.Situation;
import Oops.backend.domain.post.service.PostCommandService;
import Oops.backend.domain.post.service.PostLikeQueryService;
import Oops.backend.domain.post.service.PostQueryService;
import Oops.backend.domain.postGroup.dto.GetPostGroupResponse;
import Oops.backend.domain.postGroup.entity.PostGroup;
import Oops.backend.domain.randomTopic.dto.RandomTopicResponse;
import Oops.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostGroupQueryServiceImpl implements PostGroupQueryService {

    private final PostQueryService postQueryService;
    private final PostCommandService postCommandService;
    private final S3ImageService s3ImageService;
    private final PostLikeQueryService postLikeQueryService;
    private final CommentLikeService commentLikeService;

    @Override
    @Transactional
    public GetPostGroupResponse getPostGroup(User user, Long postId) {

        // 조회하려는 게시글을 DB에서 조회
        Post post = postQueryService.findPost(postId);

        // 게시글 작성자의 프로필 사진 가져오기
        String profileImage = s3ImageService.getPreSignedUrl(post.getUser().getProfileImageUrl());

        // 해당 게시글의 웁스 중, 극복 중, 극복 완료를 모두 조회하는 PostGroup 조회
        PostGroup postGroup = post.getPostGroup();

        if (postGroup == null){
            throw new GeneralException(ErrorStatus._BAD_REQUEST, "게시글을 찾을 수 없습니다.");
        }

        // 카테고리 DTO 생성
        CategoryResponse.CategoryResponseDto categoryResponseDto
                = CategoryResponse.CategoryResponseDto.from(postGroup.getCategory());

        // 조회한 게시글 조회수 +1
        postCommandService.watchPost(postId);

        // 3개의 게시글 DTO 초기화
        PostResponse.PostViewDto postViewDtoFailure = null;
        PostResponse.PostViewDto postViewDtoOvercoming = null;
        PostResponse.PostViewDto postViewDtoOvercome = null;

        // 웁스 중인 게시글 조회
        Post postFailure = postQueryService.findPostFromPostGroupBySituation(postGroup, Situation.OOPS)
                .orElse(null);

        // 게시글이 조회 되었다면 DTO 래핑
        if (postFailure != null) {
            postViewDtoFailure = toPostViewDto(postFailure, user, profileImage);
        }

        // 극복 중인 게시글 조회
        Post postOvercoming = postQueryService.findPostFromPostGroupBySituation(postGroup, Situation.OVERCOMING)
                .orElse(null);

        // 게시글이 조회 되었다면 DTO 래핑
        if (postOvercoming != null) {
            postViewDtoOvercoming = toPostViewDto(postOvercoming, user, profileImage);
        }

        // 극복 완료인 게시글 조회
        Post postOvercome = postQueryService.findPostFromPostGroupBySituation(postGroup, Situation.OVERCOME)
                .orElse(null);

        // 게시글이 조회 되었다면 DTO 래핑
        if (postOvercome != null) {
            postViewDtoOvercome = toPostViewDto(postOvercome, user, profileImage);
        }

        RandomTopicResponse.RandomTopicOfPostDto randomTopic =
                RandomTopicResponse.RandomTopicOfPostDto.from(postFailure.getTopic());

        return GetPostGroupResponse.of(postGroup.getId(),
                categoryResponseDto,
                randomTopic,
                postViewDtoFailure,
                postViewDtoOvercoming,
                postViewDtoOvercome);
    }

    public PostResponse.PostViewDto toPostViewDto(Post post, User user, String profileImage){

        boolean liked = postLikeQueryService.findPostLike(user, post);

        List<String> images = post.getImages().stream()
                .map(s3ImageService::getPreSignedUrl)
                .toList();

        List<CommentResponse> comments = post.getComments().stream()
                .map((comment) -> CommentResponse.of(comment, commentLikeService.existsCommentLike(comment, user)))
                .toList();

        return PostResponse.PostViewDto.of(post, images, profileImage, liked, comments);
    }
}

package Oops.backend.domain.comment.service;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.comment.dto.CommentResponse;
import Oops.backend.domain.comment.entity.Comment;
import Oops.backend.domain.comment.entity.SortType;
import Oops.backend.domain.comment.repository.CommentRepository;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.service.PostQueryService;
import Oops.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentQueryService {

    private final CommentRepository commentRepository;
    private final PostQueryService postQueryService;
    private final CommentLikeService commentLikeService;

    public Comment findComment(Long commentId){

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND));

        return comment;
    }

    @Transactional
    public List<CommentResponse> findCommentsOfPost(Long postId, User user, SortType sortType){

        Post post = postQueryService.findPost(postId);

        // 1. 일반 댓글(parent가 null) 조회 및 정렬
        List<Comment> parentComments;
        if (sortType == SortType.LIKE) {
            // 좋아요순 정렬 (좋아요가 같으면 최신순으로 2차 정렬)
            parentComments = commentRepository.findByPostAndParentIsNullOrderByLikesDescCreatedAtDesc(post);
        } else {
            // 최신순 정렬
            parentComments = commentRepository.findByPostAndParentIsNullOrderByCreatedAtDesc(post);
        }

        // 2. 모든 답글 조회 (parent가 null이 아님) - 이미 최신순으로 정렬됨
        List<Comment> allReplyComments = commentRepository.findByPostAndParentIsNotNullOrderByCreatedAtDesc(post);
        
        // 3. 답글을 parentId별로 그룹화 (Map<parentId, List<답글>>)
        // 답글들은 이미 최신순으로 정렬되어 있으므로 그대로 사용
        Map<Long, List<Comment>> repliesByParentId = allReplyComments.stream()
                .collect(Collectors.groupingBy(
                        reply -> reply.getParent().getId(),
                        Collectors.toList()
                ));

        // 4. 일반 댓글과 그 답글들을 순서대로 변환
        // 결과: [댓글1, 댓글1-답글1, 댓글1-답글2, 댓글2, 댓글2-답글1, 댓글3, ...]
        List<CommentResponse> result = new ArrayList<>();
        
        for (Comment parentComment : parentComments) {
            // 일반 댓글 추가
            result.add(CommentResponse.of(parentComment, commentLikeService.existsCommentLike(parentComment, user)));
            
            // 해당 일반 댓글의 답글들 추가 (이미 최신순으로 정렬되어 있음)
            List<Comment> replies = repliesByParentId.getOrDefault(parentComment.getId(), Collections.emptyList());
            for (Comment reply : replies) {
                result.add(CommentResponse.of(reply, commentLikeService.existsCommentLike(reply, user)));
            }
        }

        return result;
    }


}

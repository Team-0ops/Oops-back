package Oops.backend.domain.post.dto;

import Oops.backend.domain.comment.dto.CommentResponse;
import Oops.backend.domain.comment.model.CommentType;
import Oops.backend.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponse {
    /**
     * 홈화면에 출력될 각 실패담의 preview 정보
     */
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostPreviewDto {
        Long postId;
        String title;
        String content;
        String categoryName;
        int likes;
        int comments;
        int views;
        String image;  // 대표 이미지 한 장

        public static PostPreviewDto from(Post post) {
            return PostResponse.PostPreviewDto.builder()
                    .postId(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .categoryName(post.getCategory().getName())
                    .likes(post.getLikes())
                    .comments(post.getComments() != null ? post.getComments().size() : 0)
                    .views(post.getWatching())
                    .image(post.getImages() != null && !post.getImages().isEmpty() ? post.getImages().get(0) : null)
                    .build();
        }
    }

    /**
     * PostPreviewDto를 담는 리스트
     */
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostPreviewListDto {
        String name;   // 베스트 or 즐겨찾기 or 카테고리
        List<PostPreviewDto> posts;
        boolean isLast;
    }

    /**
     * 하나의 Post를 담는 DTO
     */
    @Builder
    @Getter
    @AllArgsConstructor
    public static class PostViewDto {

        Long userId;

        String nickname;

        String profileImage;

        Long postId;

        String title;

        String content;

        Integer likes;

        Integer watching;

        List<String> images;

        List<CommentResponse> comments;

        List<CommentType> wantedCommentTypes;

        LocalDateTime created_at;

        Boolean liked;

        public static PostViewDto of(Post post, List<String> images, String profileImage, Boolean liked){

            List<CommentResponse> comments = post.getComments().stream()
                    .map(CommentResponse::from)
                    .toList();

            return PostViewDto.builder()
                    .postId(post.getId())
                    .userId(post.getUser().getId())
                    .nickname(post.getUser().getUserName())
                    .profileImage(profileImage)
                    .title(post.getTitle())
                    .content(post.getContent())
                    .likes(post.getLikes())
                    .watching(post.getWatching())
                    .images(post.getImages())
                    .comments(comments)
                    .images(images)
                    .wantedCommentTypes(post.getWantedCommentTypes())
                    .created_at(post.getCreatedAt())
                    .liked(liked)
                    .build();
        }

    }
}

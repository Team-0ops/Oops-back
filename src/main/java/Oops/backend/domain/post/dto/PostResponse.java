package Oops.backend.domain.post.dto;

import Oops.backend.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}

package Oops.backend.domain.mypage.dto.response;

import Oops.backend.domain.post.dto.PostSummaryDto;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/*
@Getter
@Builder
@AllArgsConstructor
public class OtherProfileResponseDto {
    private String userName;
    private List<OtherUserPostDto> posts;
    private List<PostSummaryDto> bestFailers;

    public static OtherProfileResponseDto from(User user, List<Post> posts, List<Post> bestFailers) {
        return OtherProfileResponseDto.builder()
                .userName(user.getUserName())
                .posts(posts.stream().map(OtherUserPostDto::from).toList())
                .bestFailers(bestFailers.stream().map(PostSummaryDto::from).toList())
                .build();
    }
}*/
@Getter
@Builder
@AllArgsConstructor
public class OtherProfileResponseDto {
    private String userName;
    private List<OtherUserPostDto> posts;
    private List<PostSummaryDto> bestFailers;

    // ✅ 새 오버로드: 서비스에서 이미 DTO로 만든 걸 받는 버전
    public static OtherProfileResponseDto from(User user,
                                               List<OtherUserPostDto> posts,
                                               List<Post> bestFailers) {
        return OtherProfileResponseDto.builder()
                .userName(user.getUserName())
                .posts(posts)
                .bestFailers(bestFailers.stream().map(PostSummaryDto::from).toList())
                .build();
    }
    /*
    // (선택) 기존 시그니처는 유지해도 되지만, Presigned URL이 빠진 이미지가 내려감에 주의!
    @Deprecated
    public static OtherProfileResponseDto from(User user, List<Post> posts, List<Post> bestFailers) {
        return OtherProfileResponseDto.builder()
                .userName(user.getUserName())
                .posts(posts.stream()
                        // ⚠️ 이 경로로 부르면 이미지 Presigned URL이 생성되지 않음
                        .map(p -> OtherUserPostDto.from(p, k -> k)) // 키 그대로(임시)
                        .toList())
                .bestFailers(bestFailers.stream().map(PostSummaryDto::from).toList())
                .build();
    }*/
}

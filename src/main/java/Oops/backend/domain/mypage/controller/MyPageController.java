package Oops.backend.domain.mypage.controller;

import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.mypage.dto.request.UpdateProfileRequestDto;
import Oops.backend.domain.mypage.service.MyPageCommandService;
import Oops.backend.domain.mypage.service.MyPageQueryService;
import Oops.backend.domain.post.model.Situation;
import Oops.backend.domain.user.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/my-page")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageQueryService myPageQueryService;
    private final MyPageCommandService myPageCommandService;


    @Operation(summary = "내가 쓴 실패담 조회", description = "내가 작성한 실패담 목록을 조회합니다. 선택적으로 카테고리 ID로 필터링할 수 있습니다.")
    @GetMapping("/posts")
    public ResponseEntity<BaseResponse> getMyPosts(
            @Parameter(hidden = true) User user,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) Situation situation
    ) {
        return BaseResponse.onSuccess(
                SuccessStatus._OK,
                myPageQueryService.getMyPosts(user, categoryId, topicId, situation)
        );
    }

    @Operation(summary = "내가 쓴 교훈 조회", description = "내가 작성한 교훈(레슨) 목록을 조회합니다. 선택적으로 태그로 필터링할 수 있습니다.")
    @GetMapping("/lessons")
    public ResponseEntity<BaseResponse> getMyLessons(@Parameter(hidden = true) User user,
                                                     @RequestParam(required = false) String tag) {
        return BaseResponse.onSuccess(SuccessStatus._OK,
                myPageQueryService.getMyLessons(user, tag));
    }

    //내 정보 조회
    @Operation(summary = "내 프로필 조회", description = "내 이메일, 닉네임, 포인트, 신고 수 등의 프로필 정보를 조회합니다.")
    @GetMapping("/profile")
    public ResponseEntity<BaseResponse> getMyProfile(
            @Parameter(hidden = true) User user) {
        return BaseResponse.onSuccess(SuccessStatus._OK, myPageQueryService.getMyProfile(user));
    }


    @Operation(
            summary = "내 프로필 수정",
            description = """
    닉네임과 프로필 이미지를 수정합니다.
    - `data`: 닉네임 JSON 문자열 (안 바꿀거면 null로 쓰면 됨!)
    - `profileImage`: 이미지 파일 (선택)

    **예시(JSON)**:
    ```json
    { "userName": "새닉네임" }
    ```
    """
    )
    @PatchMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse> updateMyProfile(
            @Parameter(hidden = true) User user,
            @RequestPart(value = "data") String data,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) throws JsonProcessingException {
        UpdateProfileRequestDto dto = null;
        if (data != null && !data.isBlank()) {
            dto = new ObjectMapper().readValue(data, UpdateProfileRequestDto.class);
        }

        myPageCommandService.updateMyProfile(user, dto, profileImage);
        return BaseResponse.onSuccess(SuccessStatus._OK);
    }



    @Operation(summary = "다른 사람의 프로필 조회", description = "userId를 기반으로 다른 사용자의 닉네임, 게시글등의 프로필 정보를 조회합니다.")
    @GetMapping("/profile/{userId}")
    public ResponseEntity<BaseResponse> getOtherUserProfile(@PathVariable Long userId) {
        return BaseResponse.onSuccess(
                SuccessStatus._OK,
                myPageQueryService.getOtherUserProfile(userId)
        );
    }

}

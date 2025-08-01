package Oops.backend.domain.mypage.controller;

import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.mypage.dto.request.UpdateProfileRequestDto;
import Oops.backend.domain.mypage.dto.response.MyProfileResponseDto;
import Oops.backend.domain.mypage.service.MyPageCommandService;
import Oops.backend.domain.mypage.service.MyPageQueryService;
import Oops.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/my-page")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageQueryService myPageQueryService;
    private final MyPageCommandService myPageCommandService;


    @Operation(summary = "내가 쓴 실패담 조회", description = "내가 작성한 실패담 목록을 조회합니다. 선택적으로 카테고리 ID로 필터링할 수 있습니다.")
    @GetMapping("/posts")
    public ResponseEntity<BaseResponse> getMyPosts(@AuthenticatedUser User user,
                                                   @RequestParam(required = false) Long categoryId) {
        return BaseResponse.onSuccess(SuccessStatus._OK,
                myPageQueryService.getMyPosts(user, categoryId));
    }

    @Operation(summary = "내가 쓴 교훈 조회", description = "내가 작성한 교훈(레슨) 목록을 조회합니다. 선택적으로 태그로 필터링할 수 있습니다.")
    @GetMapping("/lessons")
    public ResponseEntity<BaseResponse> getMyLessons(@AuthenticatedUser User user,
                                                     @RequestParam(required = false) String tag) {
        return BaseResponse.onSuccess(SuccessStatus._OK,
                myPageQueryService.getMyLessons(user, tag));
    }

    //내 정보 조회
    @Operation(summary = "내 프로필 조회", description = "내 이메일, 닉네임, 포인트 등의 프로필 정보를 조회합니다.")
    @GetMapping("/profile")
    public ResponseEntity<BaseResponse> getMyProfile(
            @AuthenticatedUser User user) {
        System.out.println("controller" + user);
        return BaseResponse.onSuccess(SuccessStatus._OK, MyProfileResponseDto.from(user));
    }


    @Operation(summary = "내 프로필 수정", description = "닉네임 등 내 프로필 정보를 수정합니다.")
    @PatchMapping("/profile")
    public ResponseEntity<BaseResponse> updateMyProfile(
            @AuthenticatedUser User user,
            @Valid @RequestBody UpdateProfileRequestDto requestDto) {
        myPageCommandService.updateProfile(user, requestDto);
        return BaseResponse.onSuccess(SuccessStatus._OK);
    }
}

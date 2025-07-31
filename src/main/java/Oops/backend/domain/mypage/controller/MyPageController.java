package Oops.backend.domain.mypage.controller;

import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.mypage.dto.request.UpdateProfileRequestDto;
import Oops.backend.domain.mypage.dto.response.MyProfileResponseDto;
import Oops.backend.domain.mypage.service.MyPageCommandService;
import Oops.backend.domain.mypage.service.MyPageQueryService;
import Oops.backend.domain.user.entity.User;
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


    // 내가 쓴 실패담 조회
    @GetMapping("/posts")
    public ResponseEntity<BaseResponse> getMyPosts(@AuthenticatedUser User user,
                                                   @RequestParam(required = false) Long categoryId) {
        return BaseResponse.onSuccess(SuccessStatus._OK,
                myPageQueryService.getMyPosts(user, categoryId));
    }

    // 내가 쓴 교훈 조회
    @GetMapping("/lessons")
    public ResponseEntity<BaseResponse> getMyLessons(@AuthenticatedUser User user,
                                                     @RequestParam(required = false) String tag) {
        return BaseResponse.onSuccess(SuccessStatus._OK,
                myPageQueryService.getMyLessons(user, tag));
    }

    //내 정보 조회
    @GetMapping("/profile")
    public ResponseEntity<BaseResponse> getMyProfile(
            @AuthenticatedUser User user) {
        System.out.println("controller" + user);
        return BaseResponse.onSuccess(SuccessStatus._OK, MyProfileResponseDto.from(user));
    }


    //내 정보 수정
    @PatchMapping("/profile")
    public ResponseEntity<BaseResponse> updateMyProfile(
            @AuthenticatedUser User user,
            @Valid @RequestBody UpdateProfileRequestDto requestDto) {
        myPageCommandService.updateProfile(user, requestDto);
        return BaseResponse.onSuccess(SuccessStatus._OK);
    }
}

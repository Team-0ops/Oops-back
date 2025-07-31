package Oops.backend.domain.randomTopic.controller;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.post.dto.PostResponse;
import Oops.backend.domain.randomTopic.Service.RandomTopicService;
import Oops.backend.domain.randomTopic.dto.RandomTopicResponse;
import Oops.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feeds")
public class RandomTopicController {

    private final RandomTopicService randomTopicService;

    @GetMapping("/banners")
    @Operation(summary = "홈화면 배너 API",description = "홈화면의 배너에 필요한 정보를 조회하는 api입니다. ")
    public ResponseEntity<BaseResponse> getBannarInfo (@Parameter(hidden = true) @AuthenticatedUser User user) {

        RandomTopicResponse.BannarsInfoDto result = randomTopicService.getBannarInfo(user);
        return BaseResponse.onSuccess(SuccessStatus._OK, result);
    }
}
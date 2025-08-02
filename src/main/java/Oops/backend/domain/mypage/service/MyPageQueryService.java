package Oops.backend.domain.mypage.service;

import Oops.backend.domain.mypage.dto.response.MyLessonResponseDto;
import Oops.backend.domain.mypage.dto.response.MyPostResponseDto;
import Oops.backend.domain.mypage.dto.response.MyProfileResponseDto;
import Oops.backend.domain.mypage.dto.response.OtherProfileResponseDto;
import Oops.backend.domain.user.entity.User;

import java.util.List;

public interface MyPageQueryService {
    List<MyPostResponseDto> getMyPosts(User user, Long categoryId);
    //List<MyLessonResponseDto> getMyLessons(User user, Long categoryId);
    List<MyLessonResponseDto> getMyLessons(User user, String tag);

    MyProfileResponseDto getMyProfile(User user);
    OtherProfileResponseDto getOtherUserProfile(Long userId);
}
package Oops.backend.domain.mypage.service;

import Oops.backend.domain.mypage.dto.request.UpdateProfileRequestDto;
import Oops.backend.domain.user.entity.User;

public interface MyPageCommandService {
    void updateProfile(User user, UpdateProfileRequestDto dto);
}

package Oops.backend.domain.mypage.service;

import Oops.backend.domain.mypage.dto.request.UpdateProfileRequestDto;
import Oops.backend.domain.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface MyPageCommandService {
    void updateMyProfile(User user, UpdateProfileRequestDto dto, MultipartFile profileImage);
}
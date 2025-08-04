package Oops.backend.domain.mypage.service;

import Oops.backend.config.s3.S3ImageService;
import Oops.backend.domain.mypage.dto.request.UpdateProfileRequestDto;
import Oops.backend.domain.user.entity.User;
import Oops.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MyPageCommandServiceImpl implements MyPageCommandService {

    private final UserRepository userRepository;
    private final S3ImageService s3ImageService;

    @Override
    @Transactional
    public void updateMyProfile(User user, UpdateProfileRequestDto dto, MultipartFile profileImage) {
        // 닉네임 변경
        user.setUserName(dto.getUserName());

        // 이미지 업로드 처리
        if (profileImage != null && !profileImage.isEmpty()) {
            String imageUrl = s3ImageService.upload(profileImage);
            user.setProfileImageUrl(imageUrl);
        }

        userRepository.save(user);
    }
}
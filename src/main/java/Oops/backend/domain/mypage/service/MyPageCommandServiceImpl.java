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
        // 닉네임이 있을 경우만 변경
        if (dto != null && dto.hasUserName()) {
            user.setUserName(dto.getUserName());
        }
        // 프로필 이미지 처리
        if (profileImage != null && !profileImage.isEmpty()) {
            // 기존 이미지가 있으면 삭제
            String oldImageUrl = user.getProfileImageUrl();
            if (oldImageUrl != null && !oldImageUrl.isBlank()) {
                s3ImageService.deleteImageFromS3(oldImageUrl);
            }

            // 새 이미지 업로드
            String newImageUrl = s3ImageService.upload(profileImage, "user_profile", user.getId().toString());
            user.setProfileImageUrl(newImageUrl);
        }

        userRepository.save(user);
    }
}
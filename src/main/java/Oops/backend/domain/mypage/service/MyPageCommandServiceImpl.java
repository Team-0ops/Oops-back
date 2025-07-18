package Oops.backend.domain.mypage.service;

import Oops.backend.domain.mypage.dto.request.UpdateProfileRequestDto;
import Oops.backend.domain.user.entity.User;
import Oops.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageCommandServiceImpl implements MyPageCommandService {

    private final UserRepository userRepository;

    @Transactional
    public void updateProfile(User user, UpdateProfileRequestDto dto) {
        user.setUserName(dto.getUserName());
        userRepository.save(user);
    }
}
package Oops.backend.domain.user.service;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.user.entity.User;
import Oops.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService{

    private final UserRepository userRepository;


    @Override
    public User findUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST, "회원 정보가 존재하지 않습니다."));

        return user;
    }
}

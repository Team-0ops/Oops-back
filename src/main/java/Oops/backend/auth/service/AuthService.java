package Oops.backend.auth.service;

import Oops.backend.auth.PasswordHashEncryption;
import Oops.backend.auth.dto.request.JoinDto;
import Oops.backend.common.exception.GeneralException;
import Oops.backend.domain.user.domain.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import Oops.backend.auth.repository.AuthRepository;

@Service
@AllArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;
    private final PasswordHashEncryption passwordHashEncryption;

    /*
    회원가입
     */
    public void join(JoinDto joinDto, HttpServletResponse response) {
        // 이메일이 이미 존재하는지 확인
        this.isEmailExist(joinDto.getEmail());
        String encryptedPassword = this.passwordHashEncryption.encrypt(joinDto.getPassword());

        // 이메일이 존재하지 않는다면 새로운 User 생성
        User user = User.builder()
                .email(joinDto.getEmail())
                .password(encryptedPassword)
                .username(joinDto.getUsername())
                .build();

        authRepository.save(user);
    }

    /*
    Email 유일성 확인
     */
    public void isEmailExist(String email) {
        User user = this.authRepository.findByEmail(email);
        if (user != null) {
            throw new RuntimeException("이미 존재하는 이메일 입니다.");
        }
    }

}

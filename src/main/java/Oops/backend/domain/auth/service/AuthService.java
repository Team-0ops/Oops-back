package Oops.backend.domain.auth.service;

import Oops.backend.domain.auth.JwtEncoder;
import Oops.backend.domain.auth.JwtTokenProvider;
import Oops.backend.domain.auth.PasswordHashEncryption;
import Oops.backend.domain.auth.dto.request.JoinDto;
import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.auth.dto.response.LoginResponse;
import Oops.backend.domain.user.dto.request.LoginDto;
import Oops.backend.domain.user.entity.User;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import Oops.backend.domain.auth.repository.AuthRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {
    private final AuthRepository authRepository;
    private final PasswordHashEncryption passwordHashEncryption;
    private final JwtTokenProvider jwtTokenProvider;
    /*
    회원가입
     */
    public void join(JoinDto joinDto) {
        // 이메일이 이미 존재하는지 확인
        this.isEmailExist(joinDto.getEmail());
        String encryptedPassword = this.passwordHashEncryption.encrypt(joinDto.getPassword());

        // 이메일이 존재하지 않는다면 새로운 User 생성
        User user = User.builder()
                .email(joinDto.getEmail())
                .password(encryptedPassword)
                .userName(joinDto.getUserName())
                .point(0)
                .build();

        authRepository.save(user);
    }

    /*
    Email 유일성 확인
     */
    public void isEmailExist(String email) {
        User user = this.authRepository.findByEmail(email);
        if (user != null) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST, "이미 존재하는 이메일 입니다.");
        }
    }

    /*
    login
     */
    public LoginResponse login(LoginDto loginDto, HttpServletResponse response) {
        log.info("login 진입");
        User user = this.authRepository.findByEmail(loginDto.getEmail());

        if(user == null) {
            throw new GeneralException(ErrorStatus._NOT_FOUND, "User를 찾을 수 없습니다.");
        }

        if (!passwordHashEncryption.matches(loginDto.getPassword(), user.getPassword())) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED);
        }

        String payload = user.getId().toString();
        String accessToken = jwtTokenProvider.createToken(payload);
        log.info("AccessToken: " + accessToken);
        log.info("UserName: "+ user.getUserName());
        ResponseCookie cookie = ResponseCookie.from("AccessToken", JwtEncoder.encodeJwtBearerToken(accessToken))
                .maxAge(Duration.ofMillis(1800000))
                .httpOnly(true)
                .sameSite("None")
                .secure(false)
                .path("/")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        return LoginResponse.of(user, accessToken);
    }

    @Transactional
    public void changePassword(User user, String oldPassword, String newPassword) {
        User user1 = authRepository.findByEmail(user.getEmail());
        log.info(user1.getUserName());
        if (!passwordHashEncryption.matches(oldPassword, user.getPassword())) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED, "기존 비밀번호가 일치하지 않습니다.");
        }

        String encryptedPassword = passwordHashEncryption.encrypt(newPassword);
        user.setPassword(encryptedPassword);
        authRepository.save(user);
    }


}
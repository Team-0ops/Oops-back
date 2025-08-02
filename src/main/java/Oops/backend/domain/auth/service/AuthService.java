package Oops.backend.domain.auth.service;

import Oops.backend.domain.auth.JwtEncoder;
import Oops.backend.domain.auth.JwtTokenProvider;
import Oops.backend.domain.auth.PasswordHashEncryption;
import Oops.backend.domain.auth.dto.request.JoinDto;
import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.user.dto.request.LoginDto;
import Oops.backend.domain.user.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import Oops.backend.domain.auth.repository.AuthRepository;

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
    public String login(LoginDto loginDto, HttpServletResponse response) {
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
                .sameSite("LAX")
                .secure(false)
                .path("/")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        return accessToken;
    }
}
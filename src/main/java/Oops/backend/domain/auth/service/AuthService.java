package Oops.backend.domain.auth.service;

import Oops.backend.domain.auth.*;
import Oops.backend.domain.auth.entity.RefreshToken;
import Oops.backend.domain.auth.dto.request.JoinDto;
import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.auth.dto.response.LoginResponse;
import Oops.backend.domain.auth.dto.response.TokenResponseDto;
import Oops.backend.domain.auth.repository.RefreshTokenRepository;
import Oops.backend.domain.terms.entity.RequiredType;
import Oops.backend.domain.terms.entity.Terms;
import Oops.backend.domain.terms.entity.UserAndTerms;
import Oops.backend.domain.terms.repository.TermsRepository;
import Oops.backend.domain.terms.repository.UserAndTermsRepository;
import Oops.backend.domain.user.dto.request.LoginDto;
import Oops.backend.domain.user.entity.User;
import Oops.backend.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import Oops.backend.domain.auth.dto.request.AgreeToTermDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import Oops.backend.domain.auth.repository.AuthRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {
    private final AuthRepository authRepository;
    private final PasswordHashEncryption passwordHashEncryption;
    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final TermsRepository termsRepository;
    private final UserAndTermsRepository userAndTermsRepository;
    /*
    회원가입
     */
    @Transactional
    public void join(JoinDto joinDto) {
        // 중복 이메일 체크
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
    public LoginResponse login(LoginDto loginDto, HttpServletResponse response) {
        log.info("login 진입");
        User user = this.authRepository.findByEmail(loginDto.getEmail());

        if(user == null) {
            throw new GeneralException(ErrorStatus._NOT_FOUND, "User를 찾을 수 없습니다.");
        }

        if (!passwordHashEncryption.matches(loginDto.getPassword(), user.getPassword())) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED, "비밀번호를 확인해 주세요.");
        }

        log.info("UserName: "+ user.getUserName());
        TokenResponseDto tokenResponseDto = this.createToken(user);
        setCookie(response, tokenResponseDto.getAccessToken());
        setCookieForRefreshToken(response, tokenResponseDto.getRefreshToken());

        response.addHeader("AccessToken", tokenResponseDto.getAccessToken().toString());
        response.addHeader("RefreshToken", tokenResponseDto.getRefreshToken().toString());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        log.info("RefreshToken: "+ tokenResponseDto.getRefreshToken());
        log.info("AccessToken: " + tokenResponseDto.getAccessToken());

        return LoginResponse.of(user, tokenResponseDto.getAccessToken(), tokenResponseDto.getRefreshToken());
    }

    // login
    public void setCookie(HttpServletResponse response, String accessToken) {
        ResponseCookie cookie = ResponseCookie.from("AccessToken", JwtEncoder.encode(accessToken))
                .maxAge(Duration.ofMillis(Duration.ofMinutes(30).toMillis()))
                .httpOnly(true)
                .sameSite("LAX")
                .secure(false)
                .path("/")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
    // refresh
    public void setCookieForRefreshToken(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie_refresh = ResponseCookie.from("RefreshToken", refreshToken)
                .maxAge(Duration.ofDays(14))
                .path("/")
                .httpOnly(true)
                .sameSite("LAX")
                .secure(false)
                .build();

        response.addHeader("Set-Cookie", cookie_refresh.toString());
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

    public void logout(HttpServletResponse response) {
        ResponseCookie accessCookie = ResponseCookie.from("AccessToken", null)
                .maxAge(0)
                .path("/")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("RefreshToken", null)
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .sameSite("None")
                .secure(false)
                .build();

        response.addHeader("set-cookie", accessCookie.toString());
        response.addHeader("set-cookie", refreshCookie.toString());
    }

    public TokenResponseDto refreshAccessToken(String refreshToken) {
        RefreshToken stored = findExistingRefreshToken(refreshToken);

        // 만료 검사
        validateRefreshToken(stored);

        User user = findExistingUserByRefreshToken(stored);

        String payload = String.valueOf(user.getId());
        String newAccess = accessTokenProvider.createToken(payload);

        log.info("new AccessToken: " + newAccess);
        return new TokenResponseDto(newAccess, stored.getToken());
    }

    private TokenResponseDto createToken(User user) {
        String payload = String.valueOf(user.getId());
        String accessToken = accessTokenProvider.createToken(payload);
        String refreshTokenValue = refreshTokenProvider.createRefreshToken();

        RefreshToken refreshToken = refreshTokenRepository.findByUserId(user.getId())
                .orElse(new RefreshToken(user.getId(), refreshTokenValue));

        refreshToken.setToken(refreshTokenValue);
        refreshTokenRepository.save(refreshToken);

        return new TokenResponseDto(accessToken, refreshTokenValue);
    }

    public void validateRefreshToken(RefreshToken refreshToken) {
        if (refreshTokenProvider.isTokenExpired(refreshToken.getToken())) {
            throw new GeneralException(ErrorStatus.INVALID_REFRESH_TOKEN);
        }
    }

    public RefreshToken findExistingRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken).orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_REFRESH_TOKEN));
    }
    public User findExistingUserByRefreshToken(RefreshToken refreshToken) {
        return userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

}
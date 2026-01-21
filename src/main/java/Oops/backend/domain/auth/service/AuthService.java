package Oops.backend.domain.auth.service;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.security.util.JwtTokenProvider;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.config.s3.S3ImageService;
import Oops.backend.domain.auth.dto.request.JoinDto;
import Oops.backend.domain.auth.dto.response.LoginResponse;
import Oops.backend.domain.auth.dto.response.TokenResponseDto;
import Oops.backend.domain.auth.entity.RefreshToken;
import Oops.backend.domain.auth.repository.AuthRepository;
import Oops.backend.domain.auth.repository.RefreshTokenRepository;
import Oops.backend.domain.user.dto.request.LoginDto;
import Oops.backend.domain.user.entity.User;
import Oops.backend.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;


@Service
@AllArgsConstructor
@Slf4j
public class AuthService {
    private final AuthRepository authRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final S3ImageService s3ImageService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginDto loginDto, HttpServletResponse response) {
        log.info("login 진입");
        Optional<User> user = this.authRepository.findByLoginId(loginDto.getLoginId());

        if(user == null) {
            throw new GeneralException(ErrorStatus._NOT_FOUND, "User를 찾을 수 없습니다.");
        }

        if (!passwordEncoder.matches(loginDto.getPassword(), user.get().getPassword())) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED, "비밀번호를 확인해 주세요.");
        }

        log.info("UserName: "+ user.get().getUserName());
        TokenResponseDto tokenResponseDto = this.createToken(user.get());
        setCookie(response, tokenResponseDto.getAccessToken());
        setCookieForRefreshToken(response, tokenResponseDto.getRefreshToken());

            log.info("RefreshToken: "+ tokenResponseDto.getRefreshToken());
            log.info("AccessToken: " + tokenResponseDto.getAccessToken());

            String profileImage = s3ImageService.getPreSignedUrl(user.get().getProfileImageUrl());

            return LoginResponse.of(user.get(), tokenResponseDto.getAccessToken(), tokenResponseDto.getRefreshToken(), profileImage);
        }

        @Transactional
        public void changePassword(User user, String oldPassword, String newPassword) {
            log.info(user.getUserName());
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                throw new GeneralException(ErrorStatus._UNAUTHORIZED, "기존 비밀번호가 일치하지 않습니다.");
            }

            String encryptedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encryptedPassword);
            authRepository.save(user);
        }


    // login
    public void setCookie(HttpServletResponse response, String accessToken) {
        ResponseCookie cookie = ResponseCookie.from("AccessToken", accessToken)
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
    @Transactional
    public void join(JoinDto joinDto) {
        this.isIdExist(joinDto.getLoginId());
        String encryptedPassword = this.passwordEncoder.encode(joinDto.getPassword());
        // 이메일이 존재하지 않는다면 새로운 User 생성
        User user = User.builder()
                .loginId(joinDto.getLoginId())
                .password(encryptedPassword)
                .userName(joinDto.getUserName())
                .build();

        authRepository.save(user);
    }
    private TokenResponseDto createToken(User user) {
        log.info("UserId: "+ user.getId());

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId());
        String refreshTokenValue = jwtTokenProvider.generateRefreshToken(user.getId());

        RefreshToken refreshToken = refreshTokenRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    RefreshToken rt = new RefreshToken();
                    rt.setUserId(user.getId());
                    return rt;
                });

        refreshToken.setToken(refreshTokenValue);
        refreshTokenRepository.save(refreshToken);
        return TokenResponseDto.of(accessToken, refreshTokenValue);
    }

    public void isIdExist(String loginId) {
        Optional<User> user = this.authRepository.findByLoginId(loginId) ;
        if (user.isPresent()) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST, "이미 존재하는 아이디 입니다.");
        }
    }
    public void validateRefreshToken(RefreshToken refreshToken) {
        if (jwtTokenProvider.validate(refreshToken.getToken())) {
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


    // refresh token 관련
    //    public TokenResponseDto refreshAccessToken(String refreshToken) {
//        RefreshToken stored = findExistingRefreshToken(refreshToken);
//
//        // 만료 검사
//        validateRefreshToken(stored);
//
//        User user = findExistingUserByRefreshToken(stored);
//
//        String newAccess = jwtTokenProvider.generateAccessToken(user.getId());
//
//        log.info("new AccessToken: " + newAccess);
//        return new TokenResponseDto(newAccess, stored.getToken());
//    }
}
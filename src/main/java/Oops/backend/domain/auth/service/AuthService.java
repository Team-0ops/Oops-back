package Oops.backend.domain.auth.service;

import Oops.backend.config.s3.S3ImageService;
import Oops.backend.domain.auth.*;
import Oops.backend.domain.auth.dto.response.KakaoUserDto;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import Oops.backend.domain.auth.dto.request.AgreeToTermDto;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import Oops.backend.domain.auth.repository.AuthRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
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
    private final S3ImageService s3ImageService;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    @Value("${security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${security.oauth2.client.provider.kakao.token-uri}")
    private String kakaoTokenUri;

    @Value("${security.oauth2.client.provider.kakao.user-info-uri}")
    private String kakaoUserInfoUri;

    /*
    회원가입
     */
    @Transactional
    public void join(JoinDto joinDto) {
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

        String profileImage = s3ImageService.getPreSignedUrl(user.getProfileImageUrl());

        return LoginResponse.of(user, tokenResponseDto.getAccessToken(), tokenResponseDto.getRefreshToken(), profileImage);
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

    @Transactional
    public LoginResponse kakaoLogin(String code, HttpServletResponse httpServletResponse) {
        // 1. 인가 코드로 카카오 액세스 토큰 발급 받기
        String kakaoAccessToken = getKakaoAccessToken(code);

        // 2. 카카오 액세스 토큰으로 사용자 정보 가져오기
        KakaoUserDto kakaoUser = getKakaoUserInfo(kakaoAccessToken);

        // 3. 사용자 정보로 회원가입 또는 로그인 처리
        User user = authRepository.findByEmail(kakaoUser.getEmail());

        if (user == null) {
            // 신규 회원: 회원가입 진행
            user = User.builder()
                    .email(kakaoUser.getEmail())
                    .userName(kakaoUser.getNickname())
                    .provider("KAKAO") // 소셜 로그인임을 구분
                    .build();
            authRepository.save(user);
        }

        // 4. 자체 JWT 토큰 생성
        TokenResponseDto tokenResponseDto = createToken(user);

        // 5. 쿠키에 JWT 토큰 설정
        setCookie(httpServletResponse, tokenResponseDto.getAccessToken());
        setCookieForRefreshToken(httpServletResponse, tokenResponseDto.getRefreshToken());

        String profileImage = s3ImageService.getPreSignedUrl(user.getProfileImageUrl());

        return LoginResponse.of(user, tokenResponseDto.getAccessToken(), tokenResponseDto.getRefreshToken(), profileImage);
    }

    // 인가 코드로 카카오 액세스 토큰 발급 요청
    private String getKakaoAccessToken(String code) {
        String tokenUri = "https://kauth.kakao.com/oauth/token";

        String responseBody = webClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type","authorization_code")
                        .with("client_id", kakaoClientId)          // @Value 또는 설정 주입
                        .with("redirect_uri", kakaoRedirectUri)
                        .with("code", code))
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .block();

        try {
            Map<String, Object> jsonMap = objectMapper.readValue(responseBody, Map.class);
            String token = (String) jsonMap.get("access_token");
            if (token == null) throw new GeneralException(ErrorStatus._UNAUTHORIZED, "카카오 토큰 없음");
            return token;
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR, "카카오 토큰 파싱 오류");
        }
    }


    // 카카오 액세스 토큰으로 사용자 정보 요청
    private KakaoUserDto getKakaoUserInfo(String kakaoAccessToken) {
        String responseBody = webClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .headers(h -> h.setBearerAuth(kakaoAccessToken))
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .block();

        try {
            return objectMapper.readValue(responseBody, KakaoUserDto.class);
        } catch (JsonProcessingException e) {
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR, "카카오 사용자 정보 파싱 오류");
        }
    }


}
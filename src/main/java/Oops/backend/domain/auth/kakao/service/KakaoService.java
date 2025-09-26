package Oops.backend.domain.auth.kakao.service;

import Oops.backend.domain.auth.dto.request.KakaoLoginRequestDto;
import Oops.backend.domain.auth.dto.response.TokenResponseDto;
import Oops.backend.domain.auth.entity.RefreshToken;
import Oops.backend.domain.auth.kakao.util.JwtTokenProvider;
import Oops.backend.domain.auth.repository.AuthRepository;
import Oops.backend.domain.auth.repository.RefreshTokenRepository;
import Oops.backend.domain.user.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

    private final AuthRepository authRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;


    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String kakaoTokenUri;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String kakaoUserInfoUri;

    @Transactional
    public void loginAndSetCookie(String code, String redirectUrl, HttpServletResponse response) {
        TokenResponseDto tokens = login(code, redirectUrl);

        ResponseCookie access = ResponseCookie.from("AccessToken", tokens.getAccessToken())
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .path("/")
                .maxAge(30 * 60)
                .build();

        ResponseCookie refresh = ResponseCookie.from("RefreshToken", tokens.getRefreshToken())
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .path("/")
                .maxAge(14 * 24 * 60 * 60)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, access.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refresh.toString());
    }

    @Transactional
    public TokenResponseDto login(String code, String redirectUrl) {
        log.info("login code: {}", code);
        if (code == null || code.isBlank()) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST, "code가 필요합니다.");
        }
        String redirect = (redirectUrl != null && !redirectUrl.isBlank()) ? redirectUrl : kakaoRedirectUri;

        String kakaoAccessToken = exchangeToken(code, redirect);
        KakaoUserInfo kakaoUser = fetchUserOrThrow(kakaoAccessToken);

        User user = upsertUser(kakaoUser);
        log.info("[KAKAO-LOGIN] userId={}, email={}", user.getId(), user.getEmail());
        return issueTokens(user);
    }

    @Transactional
    public TokenResponseDto login(KakaoLoginRequestDto requestDto) {
        if (requestDto == null || requestDto.getCode() == null || requestDto.getCode().isBlank()) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST, "code가 필요합니다.");
        }
        String redirect = (requestDto.getRedirectUrl() != null && !requestDto.getRedirectUrl().isBlank())
                ? requestDto.getRedirectUrl()
                : kakaoRedirectUri;

        String kakaoAccessToken = exchangeToken(requestDto.getCode(), redirect);
        KakaoUserInfo kakaoUser = fetchUserOrThrow(kakaoAccessToken);

        User user = upsertUser(kakaoUser);
        return issueTokens(user);
    }

    @Transactional
    public void logout(Long userId, String redirectUrl) {
        if (userId == null) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST, "userId가 필요합니다.");
        }
        refreshTokenRepository.deleteByUserId(userId);
        log.info("[KAKAO-LOGOUT] userId={} -> refreshToken revoked", userId);
    }


    private String exchangeToken(String code, String redirectUri) {
        log.info("exchangeToken code: {}, redirectUri: {}", code, redirectUri);
        if (kakaoTokenUri == null || !kakaoTokenUri.startsWith("http")) {
            log.error("[KAKAO-TOKEN] token-uri misconfigured: '{}'", kakaoTokenUri);
            throw new IllegalStateException("Kakao token-uri is not absolute. Check application.yml");
        }
        if (kakaoUserInfoUri == null || !kakaoUserInfoUri.startsWith("http")) {
            log.error("[KAKAO-TOKEN] user-info-uri misconfigured: '{}'", kakaoUserInfoUri);
            throw new IllegalStateException("Kakao user-info-uri is not absolute. Check application.yml");
        }
        if (redirectUri == null || !redirectUri.startsWith("http")) {
            log.error("[KAKAO-TOKEN] redirect-uri must be absolute: '{}'", redirectUri);
            throw new IllegalArgumentException("redirect_uri must be absolute");
        }
        try {
            RestTemplate rt = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code")
                    .append("&client_id=").append(enc(kakaoClientId))
                    .append("&redirect_uri=").append(enc(redirectUri))
                    .append("&code=").append(enc(code));

            if (kakaoClientSecret != null && !kakaoClientSecret.isBlank()) {
                sb.append("&client_secret=").append(enc(kakaoClientSecret));
            }

            HttpEntity<String> req = new HttpEntity<>(sb.toString(), headers);
            ResponseEntity<Map> res = rt.exchange(kakaoTokenUri, HttpMethod.POST, req, Map.class);

            Map<String, Object> body = res.getBody();
            if (body == null || body.get("access_token") == null) {
                throw new GeneralException(ErrorStatus._BAD_REQUEST, "카카오 토큰 교환 실패");
            }
            return (String) body.get("access_token");
        } catch (RestClientResponseException ex) {
            log.error("[KAKAO-TOKEN] status={}, body={}", ex.getRawStatusCode(), ex.getResponseBodyAsString(), ex);
            throw new GeneralException(ErrorStatus._BAD_REQUEST, "카카오 토큰 교환 실패");
        } catch (Exception ex) {
            log.error("[KAKAO-TOKEN] unexpected error", ex);
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR, "카카오 토큰 교환 중 오류");
        }
    }

    private KakaoUserInfo fetchUserOrThrow(String accessToken) {
        try {
            RestTemplate rt = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<Void> req = new HttpEntity<>(headers);

            ResponseEntity<Map> res = rt.exchange(kakaoUserInfoUri, HttpMethod.GET, req, Map.class);
            Map<String, Object> body = res.getBody();
            if (body == null) throw new GeneralException(ErrorStatus._BAD_REQUEST, "카카오 사용자 응답이 비어있습니다.");

            Long id = getAsLong(body, "id");
            if (id == null) throw new GeneralException(ErrorStatus._BAD_REQUEST, "카카오 사용자 ID 없음");

            Map<String, Object> account = getAsMap(body, "kakao_account");
            if (account == null) {
                throw new GeneralException(ErrorStatus._BAD_REQUEST, "kakao_account 정보가 없습니다. (필수 동의 누락)");
            }

            String email = getAsString(account, "email");
            Boolean hasEmail = getAsBoolean(account, "has_email");                 // 동의 가능 여부
            Boolean isEmailValid = getAsBoolean(account, "is_email_valid");        // 형식상 유효
            Boolean isEmailVerified = getAsBoolean(account, "is_email_verified");  // 본인인증/검증 여부

            Map<String, Object> profile = getAsMap(account, "profile");
            String nickname = profile != null ? getAsString(profile, "nickname") : null;
            String profileImageUrl = profile != null ? getAsString(profile, "profile_image_url") : null;
            Boolean isDefaultImage = profile != null ? getAsBoolean(profile, "is_default_image") : null;

            if (email == null || email.isBlank()) {
                throw new GeneralException(ErrorStatus._BAD_REQUEST,
                        "카카오 이메일 동의가 필요합니다. (scope: account_email, prompt=consent로 재동의 유도)");
            }
            if (nickname == null || nickname.isBlank()) {
                throw new GeneralException(ErrorStatus._BAD_REQUEST,
                        "카카오 닉네임 동의가 필요합니다. (scope: profile_nickname)");
            }
            if (profileImageUrl == null || profileImageUrl.isBlank()) {
                throw new GeneralException(ErrorStatus._BAD_REQUEST,
                        "카카오 프로필 이미지 동의가 필요합니다. (scope: profile_image)");
            }

            return new KakaoUserInfo(
                    id,
                    email,
                    nickname,
                    profileImageUrl,
                    hasEmail,
                    isEmailValid,
                    isEmailVerified,
                    isDefaultImage
            );
        } catch (RestClientResponseException ex) {
            log.error("[KAKAO-USER] status={}, body={}", ex.getRawStatusCode(), ex.getResponseBodyAsString(), ex);
            throw new GeneralException(ErrorStatus._BAD_REQUEST, "카카오 사용자 조회 실패");
        } catch (GeneralException ge) {
            throw ge;
        } catch (Exception ex) {
            log.error("[KAKAO-USER] unexpected error", ex);
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR, "카카오 사용자 조회 중 오류");
        }
    }

    @Transactional
    protected User upsertUser(KakaoUserInfo kuser) {
        String email = normalizeEmail(kuser);
        User existing = authRepository.findByEmail(email);

        if (existing != null) {
            User u = existing;
            boolean dirty = false;
            if (kuser.nickname() != null && !kuser.nickname().equals(u.getUserName())) {
                u.setUserName(kuser.nickname());
                dirty = true;
            }
            if (kuser.profileImageUrl() != null && !kuser.profileImageUrl().equals(u.getProfileImageUrl())) {
                u.setProfileImageUrl(kuser.profileImageUrl());
                dirty = true;
            }
            if (dirty) authRepository.save(u);
            return u;
        }

        User created = User.builder()
                .email(email)
                .userName(kuser.nickname())
                .provider("KAKAO")
                .profileImageUrl(kuser.profileImageUrl())
                .build();
        return authRepository.save(created);
    }

    protected TokenResponseDto issueTokens(User user) {
        String access  = jwtTokenProvider.generateAccessToken(user.getId());
        String refresh = jwtTokenProvider.generateRefreshToken(user.getId());

        RefreshToken rt = refreshTokenRepository.findByUserId(user.getId())
                .orElseGet(() -> RefreshToken.of(user.getId(), refresh));
        rt.setToken(refresh);
        refreshTokenRepository.save(rt);

        return TokenResponseDto.of(access, refresh);
    }


    // 이메일 미동의 시 placeholder 생성
    private String normalizeEmail(KakaoUserInfo kakaoUser) {
        String email = kakaoUser.email();
        if (email == null || email.isBlank()) {
            email = "kakao_" + kakaoUser.id() + "@placeholder.kakao";
            log.warn("[KAKAO] 이메일 미제공 → placeholder 사용: {}", email);
        }
        return email;
    }

    private String enc(String v) {
        return URLEncoder.encode(v, StandardCharsets.UTF_8);
    }
    @SuppressWarnings("unchecked")
    private Map<String, Object> getAsMap(Map<String, Object> src, String key) {
        Object v = src != null ? src.get(key) : null;
        return (v instanceof Map<?, ?> m) ? (Map<String, Object>) m : null;
    }
    private String getAsString(Map<String, Object> src, String key) {
        Object v = src != null ? src.get(key) : null;
        return (v == null) ? null : String.valueOf(v);
    }
    private Boolean getAsBoolean(Map<String, Object> src, String key) {
        Object v = src != null ? src.get(key) : null;
        if (v instanceof Boolean b) return b;
        if (v instanceof String s) return Boolean.parseBoolean(s);
        if (v instanceof Number n) return n.intValue() != 0;
        return null;
    }
    private Long getAsLong(Map<String, Object> src, String key) {
        Object v = src != null ? src.get(key) : null;
        if (v instanceof Number n) return n.longValue();
        try { return v != null ? Long.parseLong(String.valueOf(v)) : null; } catch (Exception e) { return null; }
    }

    /** 내부 전용 record */
    private record KakaoUserInfo(
            Long id,
            String email,
            String nickname,
            String profileImageUrl,
            Boolean hasEmail,
            Boolean isEmailValid,
            Boolean isEmailVerified,
            Boolean isDefaultImage
    ) {}
}

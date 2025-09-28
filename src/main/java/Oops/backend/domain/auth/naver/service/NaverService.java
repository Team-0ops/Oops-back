package Oops.backend.domain.auth.naver.service;

import Oops.backend.common.security.token.TokenService;
import Oops.backend.domain.auth.dto.request.NaverLoginRequestDto;
import Oops.backend.domain.auth.dto.response.NaverUserInfo;
import Oops.backend.domain.auth.dto.response.TokenResponseDto;
import Oops.backend.domain.auth.entity.SocialAccount;
import Oops.backend.domain.auth.repository.SocialAccountRepository;
import Oops.backend.domain.auth.repository.AuthRepository;
import Oops.backend.domain.user.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class NaverService {
    private final SocialAccountRepository socialAccountRepository;
    private final AuthRepository authRepository;
    private final TokenService tokenService;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String naverRedirectUri;

    @Value("${spring.security.oauth2.client.provider.naver.token-uri:https://nid.naver.com/oauth2.0/token}")
    private String naverTokenUri;

    @Value("${spring.security.oauth2.client.provider.naver.user-info-uri:https://openapi.naver.com/v1/nid/me}")
    private String naverUserInfoUri;
    private static final String PROVIDER_NAVER = "NAVER";

    @Transactional
    public void loginAndSetCookie(String code, String state, String redirectUrl, HttpServletResponse response) {
        TokenResponseDto tokens = login(code, state, redirectUrl);
        tokenService.setLoginCookies(response, tokens);
    }

    @Transactional
    public TokenResponseDto login(NaverLoginRequestDto requestDto) {
        if (requestDto == null || !StringUtils.hasText(requestDto.getCode())) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST, "code가 필요합니다.");
        }
        String redirect = StringUtils.hasText(requestDto.getRedirectUrl())
                ? requestDto.getRedirectUrl()
                : naverRedirectUri;
        return login(requestDto.getCode(), requestDto.getState(), redirect);
    }


    @Transactional
    public void logout(Long userId, String redirectUrl, HttpServletResponse response) {
        if (userId == null) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST, "userId가 필요합니다.");
        }
        tokenService.revokeAllAndClearCookies(userId, response);
    }
    @Transactional
    public TokenResponseDto login(String code, String redirectUrl) {
        log.info("login code: {}", code);
        if (code == null || code.isBlank()) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST, "code가 필요합니다.");
        }
        String redirect = (redirectUrl != null && !redirectUrl.isBlank()) ? redirectUrl : naverRedirectUri;

        String kakaoAccessToken = exchangeToken(code, null,redirect);
        NaverUserInfo kakaoUser = fetchUserOrThrow(kakaoAccessToken);

        User user = loginOrLink(kakaoUser);
        log.info("[KAKAO-LOGIN] userId={}, email={}", user.getId(), user.getEmail());
        return tokenService.issue(user);
    }

    @Transactional
    public TokenResponseDto login(String code, String state, String redirectUrl) {
        log.info("naver login code: {}, state: {}", code, state);
        if (!StringUtils.hasText(code)) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST, "code가 필요합니다.");
        }
        String redirect = StringUtils.hasText(redirectUrl) ? redirectUrl : naverRedirectUri;

        String accessToken = exchangeToken(code, state, redirect);
        NaverUserInfo naverUser = fetchUserOrThrow(accessToken);

        User user = loginOrLink(naverUser);
        log.info("[NAVER-LOGIN] userId={}, email={}", user.getId(), user.getEmail());
        return tokenService.issue(user);
    }

    private String exchangeToken(String code, String state, String redirectUri) {
        log.info("exchangeToken(code,state,redirectUri): {}, {}, {}", mask(code), state, redirectUri);

        if (naverTokenUri == null || !naverTokenUri.startsWith("http")) {
            log.error("[NAVER-TOKEN] token-uri misconfigured: '{}'", naverTokenUri);
            throw new IllegalStateException("Naver token-uri is not absolute. Check application.yml");
        }
        if (naverUserInfoUri == null || !naverUserInfoUri.startsWith("http")) {
            log.error("[NAVER-TOKEN] user-info-uri misconfigured: '{}'", naverUserInfoUri);
            throw new IllegalStateException("Naver user-info-uri is not absolute. Check application.yml");
        }
        if (redirectUri == null || !redirectUri.startsWith("http")) {
            log.error("[NAVER-TOKEN] redirect-uri must be absolute: '{}'", redirectUri);
            throw new IllegalArgumentException("redirect_uri must be absolute");
        }

        try {
            RestTemplate rt = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code")
                    .append("&client_id=").append(enc(naverClientId))
                    .append("&client_secret=").append(enc(naverClientSecret))
                    .append("&code=").append(enc(code))
                    .append("&redirect_uri=").append(enc(redirectUri));
            if (StringUtils.hasText(state)) {
                sb.append("&state=").append(enc(state));
            }

            HttpEntity<String> req = new HttpEntity<>(sb.toString(), headers);
            ResponseEntity<Map> res = rt.exchange(naverTokenUri, HttpMethod.POST, req, Map.class);

            Map<String, Object> body = res.getBody();
            if (body == null || body.get("access_token") == null) {
                throw new GeneralException(ErrorStatus._BAD_REQUEST, "네이버 토큰 교환 실패");
            }
            return (String) body.get("access_token");
        } catch (RestClientResponseException ex) {
            log.error("[NAVER-TOKEN] status={}, body={}", ex.getRawStatusCode(), ex.getResponseBodyAsString(), ex);
            throw new GeneralException(ErrorStatus._BAD_REQUEST, "네이버 토큰 교환 실패");
        } catch (Exception ex) {
            log.error("[NAVER-TOKEN] unexpected error", ex);
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR, "네이버 토큰 교환 중 오류");
        }
    }

    @Transactional
    protected User loginOrLink(NaverUserInfo naverUser) {
        final String providerId = naverUser.getId();
        final String email      = naverUser.getEmail();
        final String nickname   = naverUser.getNickname();
        final String profileUrl = naverUser.getProfileImage();

        var socialOpt = socialAccountRepository
                .findByProviderAndProviderId(PROVIDER_NAVER, providerId);
        if (socialOpt.isPresent()) {
            return socialOpt.get().getUser();
        }

        if (email != null && !email.isBlank()) {
            var userOpt = authRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                var user = userOpt.get();
                attachSocial(user, PROVIDER_NAVER, providerId, email);
                if (user.getUserName() == null || user.getUserName().isBlank()) {
                    user.setUserName(nickname);
                }
                if (profileUrl != null && (user.getProfileImageUrl() == null || user.getProfileImageUrl().isBlank())) {
                    user.setProfileImageUrl(profileUrl);
                }
                return user;
            }
        }

        var newUser = authRepository.save(
                User.builder()
                        .email(email)
                        .userName(nickname)
                        .profileImageUrl(profileUrl)
                        .build()
        );
        attachSocial(newUser, PROVIDER_NAVER, providerId, email);
        return newUser;
    }

    private void attachSocial(User user, String provider, String providerId, String emailFromProvider) {
        var sa = new SocialAccount();
        sa.setUser(user);
        sa.setProvider(provider);
        sa.setProviderId(providerId);
        sa.setEmailFromProvider(emailFromProvider);
        socialAccountRepository.save(sa);
    }

    private NaverUserInfo fetchUserOrThrow(String accessToken) {
        try {
            RestTemplate rt = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<Void> req = new HttpEntity<>(headers);
            ResponseEntity<Map> res = rt.exchange(naverUserInfoUri, HttpMethod.GET, req, Map.class);

            Map<String, Object> body = res.getBody();
            if (body == null) {
                throw new GeneralException(ErrorStatus._BAD_REQUEST, "네이버 사용자 정보 조회 실패");
            }
            Object responseObj = body.get("response");
            if (!(responseObj instanceof Map)) {
                throw new GeneralException(ErrorStatus._BAD_REQUEST, "네이버 사용자 정보 응답 포맷 오류");
            }
            Map<String, Object> profile = (Map<String, Object>) responseObj;

            String id = str(profile.get("id"));
            String email = str(profile.get("email"));
            String nickname = str(profile.get("nickname"));
            String profileImage = str(profile.get("profile_image"));

            if (!StringUtils.hasText(id)) {
                throw new GeneralException(ErrorStatus._BAD_REQUEST, "네이버 사용자 id 없음");
            }

            return NaverUserInfo.builder()
                    .id(id)
                    .email(email)
                    .nickname(StringUtils.hasText(nickname) ? nickname : "NaverUser")
                    .profileImage(profileImage)
                    .build();

        } catch (RestClientResponseException ex) {
            log.error("[NAVER-USERINFO] status={}, body={}", ex.getRawStatusCode(), ex.getResponseBodyAsString(), ex);
            throw new GeneralException(ErrorStatus._BAD_REQUEST, "네이버 사용자 정보 조회 실패");
        } catch (Exception ex) {
            log.error("[NAVER-USERINFO] unexpected error", ex);
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR, "네이버 사용자 정보 조회 중 오류");
        }
    }

    private User upsertUser(NaverUserInfo naverUser) {

        String email = StringUtils.hasText(naverUser.getEmail())
                ? naverUser.getEmail()
                : null;

        Optional<User> found = (email != null)
                ? authRepository.findByEmail(email)
                : authRepository.findByProviderAndProviderId("NAVER", naverUser.getId());

        return found.orElseGet(() -> authRepository.save(
                User.builder()
                        .email(email)
                        .userName(naverUser.getNickname())
                        .provider("NAVER")
                        .providerId(naverUser.getId())
                        .profileImageUrl(naverUser.getProfileImage())
                        .build()
        ));
    }

    private static String enc(String v) {
        return UriUtils.encode(v, StandardCharsets.UTF_8);
    }

    private static String str(Object o) {
        return o == null ? null : String.valueOf(o);
    }

    private static String mask(String v) {
        if (!StringUtils.hasText(v)) return v;
        return v.length() <= 6 ? "******" : v.substring(0, 3) + "****" + v.substring(v.length() - 2);
    }
}
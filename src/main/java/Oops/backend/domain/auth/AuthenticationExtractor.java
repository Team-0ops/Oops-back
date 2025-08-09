package Oops.backend.domain.auth;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
public class AuthenticationExtractor {
    private static final String TOKEN_COOKIE_NAME = "AccessToken";


    public static String extractTokenFromRequest(final HttpServletRequest request) {
        // 1. Authorization 헤더 우선
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("AccessToken".equals(cookie.getName())) {
                    try {
                        String decoded = JwtEncoder.decode(cookie.getValue());
                        log.info("AccessToken 쿠키에서 디코딩 성공: {}", decoded);
                        return decoded;
                    } catch (Exception e) {
                        log.error("AccessToken 쿠키 디코딩 실패: {}", e.getMessage());
                        return null;
                    }
                }
            }
        }

        // 2. Authorization 헤더에서 찾기
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String decoded = JwtEncoder.decodeJwtBearerToken(authHeader);
                log.info("Authorization 헤더에서 디코딩 성공: {}", decoded);
                return decoded;
            } catch (Exception e) {
                log.error("Authorization 헤더 디코딩 실패: {}", e.getMessage());
                return null;
            }
        }

        log.warn("AccessToken을 쿠키나 헤더에서 찾을 수 없습니다.");
        return "AccessToken 을 찾을 수 없습니다.";


    }
}

/*
    public static String extractTokenFromRequest(final HttpServletRequest request) {
        if (request.getCookies() == null) {
            System.out.println("쿠키가 없습니다.");
            throw new GeneralException(ErrorStatus.INVALID_TOKEN, "쿠키가 존재하지 않습니다.");
        }

        return Arrays.stream(request.getCookies())
                .peek(cookie -> System.out.println("➡ 쿠키 확인: " + cookie.getName() + " = " + cookie.getValue()))
                .filter(cookie -> TOKEN_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(value -> value != null && !value.isEmpty())
                .findFirst()
                .map(token -> {
                    try {
                        return JwtEncoder.decodeJwtBearerToken(token);
                    } catch (Exception e) {
                        System.out.println("토큰 디코딩 실패: " + e.getMessage());
                        throw new GeneralException(ErrorStatus.INVALID_TOKEN, "토큰 디코딩에 실패했습니다.");
                    }
                })
                .orElseThrow(() -> {
                    System.out.println("AccessToken 쿠키 없음 또는 값 없음");
                    return new GeneralException(ErrorStatus.INVALID_TOKEN, "로그인 여부를 확인해주세요.");
                });
    }*/



package Oops.backend.common.security.util;

import Oops.backend.domain.auth.AuthenticationContext;
import Oops.backend.domain.user.entity.User;
import Oops.backend.domain.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationContext authenticationContext;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 화이트리스트: 인증 없이 통과시킬 경로들
    private static final AntPathMatcher matcher = new AntPathMatcher();
    private static final String[] WHITELIST = {
            "/auth/kakao/callback",
            "/auth/naver/callback",
            "/login/**",
            "/oauth2/**",
            "/public/**",
            "/favicon.ico",
            "/error",
            "/actuator/**",
            "/css/**", "/js/**", "/images/**", "/webjars/**",
            "/.well-known/**",
            "/json",
            "/json/**",
            "/devtools/**"
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        log.info("요청 url: "+path);
        for (String p : WHITELIST) {
            if (matcher.match(p, path)) return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String token = resolveAccessToken(request);
            if (token != null && !token.isBlank()) {
                if (jwtTokenProvider.validate(token)) {
                    Long userId = jwtTokenProvider.getUserId(token);

                    User user = userRepository.findById(userId)
                            .orElse(null);
                    if (user != null) {
                        authenticationContext.setPrincipal(user);

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        user, null, Collections.emptyList());
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            }
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String resolveAccessToken(HttpServletRequest request) {
        String authz = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authz != null && authz.startsWith("Bearer ")) {
            return authz.substring(7);
        }
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("AccessToken".equals(c.getName())) {
                    return c.getValue();
                }
            }
        }
        return null;
    }
}

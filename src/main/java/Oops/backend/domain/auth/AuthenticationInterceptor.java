package Oops.backend.domain.auth;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.auth.repository.AuthRepository;
import Oops.backend.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
@Slf4j
@AllArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationContext authenticationContext;
    private final AuthRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("인증 인터셉터 시작 - 요청 URL: {}", request.getRequestURI());

        try {
            String accessToken = AuthenticationExtractor.extractTokenFromRequest(request);

            // JWT 페이로드 추출
            String payload = jwtTokenProvider.getPayload(accessToken);
            log.info("JWT 페이로드 추출 성공: {}", payload);

            // 사용자 ID 변환
            Long userId = Long.parseLong(payload);
            log.info("사용자 ID 변환 성공: {}", userId);

            // 사용자 조회
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("사용자 조회 실패: ID({})", userId);
                        return new GeneralException(ErrorStatus._NOT_FOUND, "사용자 조회에 실패했습니다.");
                    });

            log.info("사용자 조회 성공: {}", user.getEmail());
            authenticationContext.setPrincipal(user);
            log.info("인증 완료, 요청 처리 진행");

            return true;
        } catch (GeneralException e) {
            log.error("인증 실패: {}", e.getMessage());

            sendErrorResponse(response, e.getMessage(), HttpStatus.UNAUTHORIZED);
            throw new GeneralException(ErrorStatus._UNAUTHORIZED, e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("사용자 ID 변환 실패: {}", e.getMessage());
            sendErrorResponse(response, "유효하지 않은 사용자 정보입니다.", HttpStatus.BAD_REQUEST);
            throw new GeneralException(ErrorStatus._NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            log.error("알 수 없는 인증 오류 발생: {}", e.getMessage(), e);
            sendErrorResponse(response, "인증 과정에서 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message, HttpStatus status) {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            response.getWriter().write(String.format("{\"error\": \"%s\"}", message));
            response.getWriter().flush();
        } catch (IOException e) {
            log.error("오류 응답 전송 실패: {}", e.getMessage());
        }
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        authenticationContext.clear();
    }
}

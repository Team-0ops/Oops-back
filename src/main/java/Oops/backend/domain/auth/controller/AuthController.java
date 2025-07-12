package Oops.backend.domain.auth.controller;
import Oops.backend.domain.auth.dto.request.JoinDto;
import Oops.backend.domain.auth.dto.request.LoginRequest;
import Oops.backend.domain.auth.service.AuthService;
import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록")
    @PostMapping("/join")
    public ResponseEntity<BaseResponse> join(@Valid @RequestBody JoinDto joinDto, HttpServletResponse response) {
        this.authService.join(joinDto, response);
        return BaseResponse.onSuccess(SuccessStatus._CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        // 실제 유저 조회 + 비밀번호 체크 필요
        Long userId = 1L; // 테스트 용도로 고정된 유저 아이디 사용
        String token = jwtUtil.generateToken(userId);
        return ResponseEntity.ok(token);
    }
}

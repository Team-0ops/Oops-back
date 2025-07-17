package Oops.backend.auth.controller;
import Oops.backend.auth.domain.LoginRequest;
import Oops.backend.domain.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        // 실제 유저 조회 + 비밀번호 체크 필요
        Long userId = 1L; // 테스트 용도로 고정된 유저 아이디 사용
        String token = jwtUtil.generateToken(userId);
        return ResponseEntity.ok(token);
    }
}

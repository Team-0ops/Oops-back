package Oops.backend.domain.auth.controller;
import Oops.backend.domain.auth.entity.VerificationPurpose;
import Oops.backend.domain.auth.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/email")
public class AuthEmailController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/send")
    public void send(@RequestParam String email, @RequestParam VerificationPurpose purpose) {
        emailVerificationService.sendCode(email, purpose);
    }

    @PostMapping("/verify")
    public String verify(@RequestParam String email,
                         @RequestParam VerificationPurpose purpose,
                         @RequestParam String code) {
        // 성공 시 verificationToken 반환
        return emailVerificationService.verifyCode(email, purpose, code);
    }
}


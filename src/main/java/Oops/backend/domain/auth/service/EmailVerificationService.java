package Oops.backend.domain.auth.service;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.auth.entity.EmailVerification;
import Oops.backend.domain.auth.entity.VerificationPurpose;
import Oops.backend.domain.auth.repository.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationRepository repo;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    private static final int CODE_TTL_MIN = 10;        // 코드 유효 10분
    private static final int TOKEN_TTL_MIN = 15;       // verify 후 토큰 유효 15분
    private static final int MAX_ATTEMPTS = 5;

    @Transactional
    public void sendCode(String email, VerificationPurpose purpose) {
        String code = generate6DigitCode();
        String hash = passwordEncoder.encode(code);

        EmailVerification ev = EmailVerification.builder()
                .email(email)
                .purpose(purpose)
                .codeHash(hash)
                .expiresAt(LocalDateTime.now().plusMinutes(CODE_TTL_MIN))
                .attemptCount(0)
                .build();

        repo.save(ev);
        sendMail(email, purpose, code);
    }

    @Transactional
    public String verifyCode(String email, VerificationPurpose purpose, String code) {
        EmailVerification ev = repo.findTopByEmailAndPurposeOrderByCreatedAtDesc(email, purpose)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND, "인증 요청이 없습니다. 먼저 인증 코드를 요청해 주세요."));

        if (ev.isCodeExpired()) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST, "인증 코드가 만료되었습니다. 다시 요청해 주세요.");
        }

        if (ev.getAttemptCount() >= MAX_ATTEMPTS) {
            throw new GeneralException(ErrorStatus._FORBIDDEN, "인증 시도 횟수를 초과했습니다. 코드를 다시 요청해 주세요.");
        }

        if (!passwordEncoder.matches(code, ev.getCodeHash())) {
            ev.setAttemptCount(ev.getAttemptCount() + 1);
            repo.save(ev);
            throw new GeneralException(ErrorStatus._BAD_REQUEST, "인증 코드가 올바르지 않습니다.");
        }

        ev.setVerifiedAt(LocalDateTime.now());
        ev.setVerificationToken(UUID.randomUUID().toString().replace("-", ""));
        ev.setTokenExpiresAt(LocalDateTime.now().plusMinutes(TOKEN_TTL_MIN));
        repo.save(ev);

        return ev.getVerificationToken();
    }

    @Transactional(readOnly = true)
    public void assertValidToken(String email, VerificationPurpose purpose, String token) {
        EmailVerification ev = repo.findByEmailAndPurposeAndVerificationToken(email, purpose, token)
                .orElseThrow(() -> new GeneralException(ErrorStatus._UNAUTHORIZED, "인증 토큰이 유효하지 않습니다."));

        if (!ev.isVerified() || ev.isTokenExpired()) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED, "인증 토큰이 만료되었거나 유효하지 않습니다.");
        }
    }

    @Transactional
    public void consumeToken(String email, VerificationPurpose purpose, String token) {
        EmailVerification ev = repo.findByEmailAndPurposeAndVerificationToken(email, purpose, token)
                .orElseThrow(() -> new GeneralException(ErrorStatus._UNAUTHORIZED, "인증 토큰이 유효하지 않습니다."));
        // 재사용 방지
        ev.setVerificationToken(null);
        ev.setTokenExpiresAt(null);
        repo.save(ev);
    }

    private void sendMail(String email, VerificationPurpose purpose, String code) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject(purpose == VerificationPurpose.SIGNUP ? "[Oops] 회원가입 인증코드" : "[Oops] 비밀번호 재설정 인증코드");
        msg.setText(
                "Oops에서 인증코드를 보내드립니다.\n" +
                        "인증코드는 " + code + " 입니다.\n" +
                        "유효시간은 " + CODE_TTL_MIN + "분입니다.\n" +
                        "정확하게 기입해주세요."
        );

        mailSender.send(msg);
    }

    private String generate6DigitCode() {
        SecureRandom r = new SecureRandom();
        int n = r.nextInt(900000) + 100000;
        return String.valueOf(n);
    }
}

package Oops.backend.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
        @Schema(description = "사용자 이메일", example = "test@example.com")
        String email,

        @Schema(description = "비밀번호", example = "1234abcd!")
        String password
) {}

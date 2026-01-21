package Oops.backend.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
        @Schema(description = "사용자 아이디", example = "test123!")
        String loginId,

        @Schema(description = "비밀번호", example = "1234abcd!")
        String password
) {}

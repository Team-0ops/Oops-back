package Oops.backend.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "이메일 사용 가능 여부 결과")
public class EmailCheckDto {

    @Schema(description = "검사한 이메일", example = "test@example.com")
    private final String email;

    @Schema(description = "사용 가능 여부", example = "true")
    private final boolean available;
}
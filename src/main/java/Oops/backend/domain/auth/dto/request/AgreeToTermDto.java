package Oops.backend.domain.auth.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public class AgreeToTermDto {
    @NotBlank(message = "약관 id가 비었습니다.")
    private UUID termId;

    @AssertTrue
    private boolean agreed;
}
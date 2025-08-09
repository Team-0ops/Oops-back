package Oops.backend.domain.auth.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AgreeToTermDto {
    @NotNull(message = "약관 id가 비었습니다.")
    private Long termId;

    @NotNull(message = "동의 여부가 비었습니다.")
    private boolean agreed;
}

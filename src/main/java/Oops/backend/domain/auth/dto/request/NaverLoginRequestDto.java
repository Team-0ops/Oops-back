package Oops.backend.domain.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NaverLoginRequestDto {
    private String code;
    private String state;
    private String redirectUrl;
}

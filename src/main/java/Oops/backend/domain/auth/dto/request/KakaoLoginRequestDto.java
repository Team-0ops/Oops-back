package Oops.backend.domain.auth.dto.request;

import lombok.Getter;

@Getter
public class KakaoLoginRequestDto {
    String code;
    String redirectUrl;
}
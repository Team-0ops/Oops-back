package com.example.mutsideout_mju.dto.response.token;

import com.example.mutsideout_mju.entity.RefreshToken;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponseDto {
    private String AccessToken;
    private String RefreshToken;
}

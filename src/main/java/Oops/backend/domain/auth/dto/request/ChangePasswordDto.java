package Oops.backend.domain.auth.dto.request;

import lombok.Getter;

@Getter
public class ChangePasswordDto {
    private String oldPassword;
    private String newPassword;
}

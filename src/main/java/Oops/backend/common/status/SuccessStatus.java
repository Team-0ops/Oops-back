package Oops.backend.common.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus {

    _OK(HttpStatus.OK, "COMMON200", "SUCCESS!"),
    _CREATED(HttpStatus.CREATED, "COMMON201", "SUCCESS!"),
    _DELETED(HttpStatus.OK, "COMMON200", "DELETED!");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
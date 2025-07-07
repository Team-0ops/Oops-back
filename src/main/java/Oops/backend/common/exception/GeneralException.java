package Oops.backend.common.exception;

import Oops.backend.common.status.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private ErrorStatus errorStatus;

    public GeneralException(String message) {
        super(message);
        this.errorStatus = ErrorStatus._INTERNAL_SERVER_ERROR;
    }

    public GeneralException(String message, Throwable cause) {
        super(message, cause);
        this.errorStatus = ErrorStatus._INTERNAL_SERVER_ERROR;
    }

    public GeneralException(Throwable cause) {
        super(cause.getMessage(), cause);
        this.errorStatus = ErrorStatus._INTERNAL_SERVER_ERROR;
    }

    // 일반적인 예외 생성 (커스텀 ErrorStatus 사용)
    public GeneralException(ErrorStatus errorStatus, String message) {
        super(message);
        this.errorStatus = errorStatus;
    }

}